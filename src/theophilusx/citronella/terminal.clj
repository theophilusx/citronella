(ns theophilusx.citronella.terminal
  "Functions to manipulate low level terminal definitions."
  (:require [theophilusx.citronella.constants :as c]
            [theophilusx.citronella.text-graphics :as tgraph]
            [theophilusx.citronella.utils :as utils])
  (:import [com.googlecode.lanterna.terminal DefaultTerminalFactory
            TerminalResizeListener Terminal]
           java.nio.charset.Charset))

(defn get-terminal
  "Get a new terminal. Returns an atom containing a map describing the terminal.
  The function accepts an optional map of options to control the type and size
  of the terminal. Supported keys in the map include

  | Key   | Description                                                      |
  |-------|------------------------------------------------------------------|
  | :type | Type of terminal. Can be `:auto`, `:text` or `:gui`. Defaults to |
  |       | `:auto`.                                                         |
  | :out  | Set the output stream for the terminal. Defaults to `System/out` |
  | :in   | Set the input stream for the terminal. Defaults to `System/in`   |
  | :charset | Specifies the character set to use. Defaults to `UTF-8`       |"
  ([]
   (get-terminal {}))
  ([{:keys [type out in charset]
     :or   {type    :auto
            out     System/out
            in      System/in
            charset "UTF-8"}}]
   (let [factory        (DefaultTerminalFactory. out in (Charset/forName charset))
         ^Terminal term (case type
                          :auto (-> factory
                                    (.createTerminal))
                          :text (-> factory
                                    (.setForceTextTerminal true)
                                    (.createTerminal))
                          :gui  (-> factory
                                    (.setAutoOpenTerminalEmulatorWindow true)
                                    (.createTerminalEmulator)))]
     (atom {:type           type
            :open           true
            :private        false
            :cursor         (let [pos (.getCursorPosition term)]
                              [(utils/column-pos pos) (utils/row-pos pos)])
            :cursor-visible true
            :size           (let [size (.getTerminalSize term)]
                              [(utils/columns size) (utils/rows size)])
            :background     "DEFAULT"
            :foreground     "DEFAULT"
            :text-graphics  (.newTextGraphics term)
            :obj            term}))))

(defn bell
  "Sound the terminal bell."
  [term]
  (.bell ^Terminal (:obj @term)))

(defn cursor-position
  "Returns a vector representing the cursor position within the terminal. The
  vector consists of two elements representing `column` and `row` position.
  The `term` argument is an atom containing the terminal definition map."
  [term]
  (let [pos (.getCursorPosition ^Terminal (:obj @term))]
    (swap! term assoc :cursor [(utils/column-pos pos) (utils/row-pos pos)])
    (:cursor @term)))

(defn set-cursor
  "Move the cursor to the specified `column` and `row` within the terminal. The
  `term` argument is an atom containing the terminal definition map."
  [term col row]
  (.setCursorPosition ^Terminal (:obj @term) col row)
  (cursor-position term))

(defn set-background
  "Set the terminal background colour. The `term` argument is an atom containing
  a terminal definition map. The `colour` argument is an ANSI colour keyword."
  [term color]
  (.setBackgroundColor ^Terminal (:obj @term) (utils/make-color color))
  (swap! term assoc :background color))

(defn set-foreground
  "Set the terminal foreground colour. The `term` argument is an atom containing
  a terminal definition map. The `colour` argument is an ANSI colour keyword."
  [term color]
  (.setForegroundColor ^Terminal (:obj @term) (utils/make-color color))
  (swap! term assoc :foreground color))

(defn reset-color-and-sgr
  "Reset colours back to defaults and remove any active SGR codes"
  [term]
  (.resetColorAndSGR ^Terminal (:obj @term)))

(defn toggle-cursor-visible
  "Toggle the visibility of the cursor. The `term` argument is an atom containing
  a terminal definition map."
  [term]
  (.setCursorVisible ^Terminal (:obj @term) (not (:cursor-visible @term)))
  (swap! term update :cursor-visible not))

(defn terminal-size
  "Returns a `vector` representing the size of the terminal. The `vector` has two
  elements, the number of columns and number of rows in the terminal. The `term`
  argument is an atom containing a terminal definition map."
  [term]
  (let [size (.getTerminalSize ^Terminal (:obj @term))]
    (swap! term assoc :size [(utils/columns size) (utils/rows size)])
    (:size @term)))

(defn clear
  "Clear the terminal. The `term` argument is an atom containing a terminal
  definition map."
  [term]
  (.clearScreen ^Terminal (:obj @term))
  (set-cursor term 0 0)
  (cursor-position term))

(defn toggle-private-mode
  "Toggle terminal private mode. The `term` argument is an atom containing a
  terminal definition map."
  [term]
  (if (:private @term)
    (.exitPrivateMode ^Terminal (:obj @term))
    (.enterPrivateMode ^Terminal (:obj @term)))
  (swap! term update :private not))

(defn flush-data
  "Flush data written to the terminal buffer to the actual terminal. The `term`
  argument is an atom containing a terminal definition map."
  [term]
  (.flush ^Terminal (:obj @term))
  (cursor-position term))

(defn put-char
  "Put the character `c` into the terminal buffer at the current cursor location.
  The `term` argument is an atom containing a terminal definition map."
  [term c]
  (.putCharacter ^Terminal (:obj @term) c)
  (cursor-position term))

(defn put-string
  "Put a string `s` to the terminal buffer. The `term` argument is an atom
  containing the terminal definition map. The optional `col` and `row` arguments
  specify the starting position for the string. If not supplied, use the current
  cursor position as the starting position. If the `sgr-vec` argument is provided,
  it is a vector of select graphics renderer keywords representing SGR mode
  modifiers. Possible values are `:blink`, `:bold`, `:bordered`, `circled`,
  `:fraktur`, `:italic`, `:reverse` and `:underline`."
  ([term s]
   (let [[col row] (:cursor @term)]
     (put-string term s col row)))
  ([term s col row]
   (tgraph/put-string (:text-graphics @term) s col row))
  ([term s col row sgr-vec]
   (tgraph/put-string (:text-graphics @term) s col row sgr-vec)))

(defn set-tg-foreground
  "Set the foreground for text graphics elements written to the buffer with
  `put-string`. The `term` argument is an atom containing a terminal definition
  map. The `colour` argument is one of the ANSI defined colours of `:black`,
  `:blue`, `:cyan`, `:default`, `:green`, `:magenta`, `:red`, `:white` and
  `:yellow`."
  [term colour]
  (tgraph/set-fg (:text-graphics @term) colour))

(defn set-tg-background
  "Set the background for text graphics elements written to the buffer with
  `put-string`. The `term` argument is an atom containing a terminal definition
  map. The `colour` argument is one of the ANSI defined colours of `:black`,
  `:blue`, `:cyan`, `:default`, `:green`, `:magenta`, `:red`, `:white` and
  `:yellow`."
  [term colour]
  (tgraph/set-bg (:text-graphics @term) colour))

(defn draw-line
  "Draw a line from `colx`/`rowx` to `coly`/`rowy`. The `term` argument is an
  atom containing a terminal definition map. The `colx` and `rowx` arguments
  specify the starting column and row while the `coly` and `rowy` arguments
  specify the column and row to end the line. The `char` argument specifies
  the character to use to draw the line."
  [term colx rowx coly rowy char]
  (tgraph/draw-line (:text-graphics @term) colx rowx coly rowy char))

(defn draw-rectangle
  "Draw a rectangle. The `term` argument is an atom containing a terminal
  definition map. The `col` and `row` arguments specify the top left corner
  column and row. The `cols` and `rows` arguments specify the number of columns
  and rows for the rectangle and the `char` character specifies the character to
  use to draw the rectangle border."
  [term col row cols rows char]
  (tgraph/draw-rectangle (:text-graphics @term) col row cols rows char))

(defn fill
  "Fill available writeable area of terminal with character using current
  background and foreground colours. The `term` argument is an atom containing
  a terminal definition map. The `char` character is the character to use as
  the fill character."
  [term char]
  (tgraph/fill (:text-graphics @term) char))

(defn fill-rectangle
  "Fill a rectangle with top let corner at `col`/`row` that is `cols` columns
  wide and `rows` rows deep using `char` character as the fill character. "
  [term col row cols rows char]
  (tgraph/fill-rectangle (:text-graphics @term) col row cols rows char))

(defn get-char
  "Return the character at the location specified by `col` and `row`. The `term`
  argument is an atom containing a terminal definition map. The `col` argument
  is an integer specifying the column within the terminal and the `row` argument
  is an integer specifying the row within the terminal. Note that not all
  terminal types support this functionality. Function will return nil when this
  functionality is not supported."
  [term col row]
  (tgraph/get-char (:text-graphics @term) col row))

(defn read-input
  "Read an input character. This is a blocking operation which reads one character
  from the terminal input. Returns a map describing the character. The map
  includes the following keys -

  | Keyword     | Description                                                  |
  |-------------|--------------------------------------------------------------|
  | :event-time | Time in milliseconds since epoch when the key was input      |
  | :type       | Keyword representing the type of key input                   |
  | :alt        | True if the alt key was depressed when the key was entered   |
  | :control    | True if the control key was depressed when key was entered   |
  | :shift      | True if the shift key was depressed when key was entered     |
  | :char       | If the key was a character key, the key value. Nil otherwise |

  The `term` argument is an atom containing a terminal definition map."
  [term]
  (let [ks (.readInput ^Terminal (:obj @term))]
    {:event-time (.getEventTime ks)
     :type (c/key-code->name (.getKeyType ks))
     :alt (.isAltDown ks)
     :control (.isCtrlDown ks)
     :shift (.isShiftDown ks)
     :char (.getCharacter ks)}))

(defn add-resize-listener
  "Add a terminal resize listener. The `listener-fn` function will be called
  when the terminal is resized. The `term` argument is an atom containing a
  terminal definition map. The `listener-fn` argument is a function which
  accepts two arguments, the new column and new row count for the resized terminal."
  [term listener-fn]
  (let [listener (reify TerminalResizeListener
                   (onResized [this terminal newSize]
                     (listener-fn (.getColumns newSize)
                                  (.getRows newSize))))]
    (.addResizeListener ^Terminal (:obj @term) listener)
    (swap! term assoc :listener listener)
    listener))

(defn remove-resize-listener
  "Remove a resize listener from a terminal. The `term` argument is an atom
  containing a terminal definition map."
  [term]
  (when (:listener @term)
    (.removeResizeListener ^Terminal (:obj @term) (:listener @term))
    (swap! term assoc :listener nil)))

(defn close
  "Close the current terminal object. The `term` argument is an atom containing
  a terminal definition map."
  [term]
  (when (:private @term)
    (toggle-private-mode term))
  (when (:listener @term)
    (remove-resize-listener term))
  (.close ^Terminal (:obj @term))
  (swap! term assoc :open false))
