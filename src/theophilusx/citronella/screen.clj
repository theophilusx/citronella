(ns theophilusx.citronella.screen
  (:require [theophilusx.citronella.terminal :as terminal]
            [theophilusx.citronella.utils :as utils]
            [theophilusx.citronella.constants :as c]
            [theophilusx.citronella.text-graphics :as tgraph])
  (:import [com.googlecode.lanterna.screen Screen TerminalScreen]
           com.googlecode.lanterna.TerminalPosition))

(defn get-screen
  "Create a new screen definition map. The `opts` argument is a map of options
  for the screen. Available option keys are -

  | Keyword  | Description                                           |
  |----------|-------------------------------------------------------|
  | :type    | The type of screen. Can be `:auto`, `:text` or `:gui` |
  |          | Defaults to `:auto`                                   |
  | :in      | The input stream to use. Defaults to `System/in`      |
  | :out     | The output stream to use. Defaults to `System/out`    |
  | :charset | The character set to use. Defaults to 'UTF-8'         |

  The function returns an atom containing a screen definition map. The keys in
  the map are -

  | Keyword        | Description                                              |
  |----------------|----------------------------------------------------------|
  | :type          | The type of the screen. Either `:auto`, `:text` or `:gui`|
  | :started       | True if the screen state has been set to start           |
  | :cursor        | Position of the cursor within the screen. Vector with    |
  |                | values for column and row                                |
  | :size          | The size of the screen. A vector with values for columns |
  |                | and rows.                                                |
  | :text-graphics | The default `TextGraphics` object for the screen         |
  | :obj           | The `Screen` object                                      |"
  ([]
   (get-screen {}))
  ([opts]
   (let [term         (terminal/get-terminal opts)
         ^Screen scrn (TerminalScreen. (:obj @term))]
     (atom {:type   (:type @term)
            :started? false
            :cursor (let [pos (.getCursorPosition scrn)]
                      [(utils/column-pos pos) (utils/row-pos pos)])
            :size   (let [size (.getTerminalSize scrn)]
                      [(utils/columns size) (utils/rows size)])
            :text-graphics (.newTextGraphics scrn)
            :obj    scrn}))))

(defn start
  "Start the screen. The `scrn` argument is an atom containing the screen
  definition map."
  [scrn]
  (when (not (:started @scrn))
    (.startScreen (:obj @scrn))
    (swap! scrn update :started? not)))

(defn stop
  "Stop the screen. The `scrn` argument is an atom containing the screen
  definition map."
  [scrn]
  (when (:started? @scrn)
    (.stopScreen (:obj @scrn))
    (swap! scrn update :started? not)))

(defn cursor-position
  "Gets the current cursor position within the screen. Sets the `:cursor` key
  in the screen definition map stored in the atom passed in as the `scrn`
  argument."
  [scrn]
  (let [pos (.getCursorPosition (:obj @scrn))]
    (swap! scrn assoc :cursor [(utils/column-pos pos) (utils/row-pos pos)])
    (:cursor @scrn)))

(defn set-cursor
  "Sets the cursor location within the screen specified in the map stored in
  the `scrn` atom argument. If no `col` and `row` arguments are provided, the
  position is set to `nil`, which effectively turns off the cursor."
  ([scrn]
   (.setCursorPosition (:obj @scrn) nil)
   (swap! scrn assoc :cursor nil))
  ([scrn col row]
   (.setCursorPosition (:obj @scrn) (TerminalPosition. col row))
   (swap! scrn assoc :cursor [col row]
          :need-refresh? true)
   (:cursor @scrn)))

(defn size
  "Return a vector representing the size of the screen as `[columns rows]`. Also
  updates the `:size` key of the screen definition map stored in the atom passed
  in as the `scrn` argument."
  [scrn]
  (let [size (.getTerminalSize (:obj @scrn))]
    (swap! scrn assoc :size [(utils/columns size) (utils/rows size)])
    (:size @scrn)))

(defn refresh
  "Refresh the screen. Copies the data from the backing buffer to the screen.
  The `scrn` argument is an atom containing the screen definition map."
  [scrn]
  (.refresh (:obj @scrn))
  (swap! scrn assoc :need-refresh? false)
  (:need-refresh? @scrn))

(defn clear
  "Clear the screen. The `scrn` argument is an atom containing the screen
  definition map."
  [scrn]
  (.clear (:obj @scrn))
  (swap! scrn assoc :need-refresh? true)
  (:need-refresh? @scrn))

(defn get-back-char
  "Get the character within the backing store buffer at location column `col`
  and row `row`. The `scrn` argument is an atom containing the screen definition
  map."
  [scrn col row]
  (let [c (.getBackCharacter (:obj @scrn) col row)]
    (when c
      {:char (utils/get-char c)
       :fg (utils/get-fg c)
       :bg (utils/get-bg c)
       :modifiers (utils/sgr-modifiers c)})))

(defn get-front-char
  "Get the character at location `col` and `row` from the screen front store.
  The `scrn` argument is a map containing the screen definition map."
  [scrn col row]
  (let [c (.getFrontCharacter (:obj @scrn) col row)]
    (when c
      {:char (utils/get-char c)
       :fg (utils/get-fg c)
       :bg (utils/get-bg c)
       :modifiers (utils/sgr-modifiers c)})))

(defn put-char
  "Put a character to the screen backing store. The `scrn` argument is an atom
  containing a screen definition map. The `col` and `row` arguments specify the
  location to put the character and `chr` is the character to put. The optional
  `fg` and `bg` arguments specify the foreground and background colors for the
  character. They can be either string color specifiers or a `TextColor` object.
  The `sgr-vec` argument is a vector of SGR keywords."
  ([scrn col row chr]
   (let [tc (utils/make-character chr)]
     (.setCharacter (:obj @scrn) col row tc)
     (swap! scrn assoc :need-refresh? true)))
  ([scrn col row chr fg bg]
   (let [tc (utils/make-character chr fg bg)]
     (.setCharacter (:obj @scrn) col row tc)
     (swap! scrn assoc :need-refresh? true)))
  ([scrn col row chr fg bg sgr-vec]
   (let [tc (utils/make-character chr fg bg sgr-vec)]
     (.setCharacter (:obj @scrn) col row tc)
     (swap! scrn assoc :need-refresh true))))

(defn put-string
  ([scrn s col row]
   (tgraph/put-string (:text-graphics @scrn) s col row)
   (swap! scrn assoc :need-refresh? true))
  ([scrn s col row sgr-vec]
   (tgraph/put-string (:text-graphics @scrn) s col row sgr-vec)
   (swap! scrn assoc :need-refresh? true)))

(defn draw-line
  [scrn colx rowx coly rowy chr]
  (tgraph/draw-line (:text-graphics @scrn) colx rowx coly rowy chr)
  (swap! scrn assoc :need-refresh? true))

(defn draw-rectangle
  [scrn col row cols rows chr]
  (tgraph/draw-rectangle (:text-graphics @scrn) col row cols rows chr)
  (swap! scrn assoc :need-refresh? true))

(defn fill
  [scrn chr]
  (tgraph/fill (:text-graphics @scrn) chr)
  (swap! scrn assoc :need-refresh? true))

(defn fill-rectangle
  [scrn col row cols rows chr]
  (tgraph/fill-rectangle (:text-graphics @scrn) col row cols rows chr)
  (swap! scrn assoc :need-refresh? true))

(defn set-tg-fg
  [scrn color]
  (tgraph/set-fg (:text-graphics @scrn) color)
  (swap! scrn assoc :need-refresh? true))

(defn set-tg-bg
  [scrn color]
  (tgraph/set-bg (:text-graphics @scrn) color)
  (swap! scrn assoc :need-refresh? true))

(defn read-input
  "Read one character of input. This is a blocking function. The `scrn` argument
  is an atom containing a screen definition map. The function returns a map
  describing the key input. The map has the following keys -

  | Keyword     | Description                                                  |
  | :event-time | Time in milliseconds since epoch when the key event occurred |
  | :type       | The key type as a keyword.                                   |
  |             | See `theophilusx.citronella.constants`                       |
  | :alt        | True if the alt modifier key was active when key entered     |
  | :control    | True if the control key was active when key was entered      |
  | :shift      | True if the shift key was active when key was entered        |
  | :char       | The character entered                                        | "
  [scrn]
  (let [ks (.readInput ^Screen (:obj @scrn))]
    {:event-time (.getEventTime ks)
     :type (c/key-code->name (.getKeyType ks))
     :alt (.isAltDown ks)
     :control (.isCtrlDown ks)
     :shift (.isShiftDown ks)
     :char (.getCharacter ks)}))

(defn close
  "Close the screen. The `scrn` argument is an atom containing the screen
  definition map."
  [scrn]
  (stop scrn)
  (.close (:obj @scrn)))
