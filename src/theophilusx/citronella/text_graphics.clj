(ns theophilusx.citronella.text-graphics
  (:import [com.googlecode.lanterna.graphics TextGraphics]
           [com.googlecode.lanterna TerminalPosition TerminalSize
            TextCharacter TextColor])
  (:require [theophilusx.citronella.constants :as c]
            [theophilusx.citronella.utils :as utils]))

(defn put-string
  "Put a string to a `TextGraphics` object. The `tg` argument is a `TextGraphics`
  object associated with a terminal or screen. The `s` argument is the string
  to write. The `col` and `row` arguments specify the column and row for the
  start position within the text graphics object. The `sgr-vec` argument is a
  vector of SGR keywords (see `theophilusx.citronella.constants`)."
  ([tg s col row]
   (.putString ^TextGraphics tg ^int col ^int row ^String s))
  ([tg s col row sgr-vec]
   (let [sgr-opts (mapv #(% c/sgr) sgr-vec)]
     (.putString ^TextGraphics tg ^int col ^int row ^String s
                 ^java.util.Collection sgr-opts))))

(defn draw-line
  "Draw a line on the `TextGraphics` object `tg` from column `colx` and row `rowx`
  to column `coly` and row `rowy` using character `chr`."
  [^TextGraphics tg colx rowx coly rowy chr]
  (.drawLine tg colx rowx coly rowy chr))

(defn draw-rectangle
  "Draw a rectangle on `TextGraphics` object `tg` starting at column `col` and
  row `row` that is `cols` columns wide and `rows` rows high."
  [^TextGraphics tg col row cols rows chr]
  (let [pos (TerminalPosition. col row)
        size (TerminalSize. cols rows)]
    (.drawRectangle tg pos size chr)))

(defn fill
  "Fill the `TextGraphics` object `tg` with the character `chr`."
  [^TextGraphics tg chr]
  (.fill tg chr))

(defn fill-rectangle
  "Fill a rectangle in a `TextGraphics` object `tg` starting at column `col`
  and row `row` that is `cols` columns wide and `rows` rows high using character
  `chr`."
  [^TextGraphics tg col row cols rows chr]
  (let [pos (TerminalPosition. col row)
        size (TerminalSize. cols rows)]
    (.fillRectangle tg pos size chr)))

(defn get-char
  "Return a `map` describing the character in the `TextGraphics` object `tg`
  located at column `col` and row `row`. The returned map consists of the
  following keys -

  | Keyword    | Description                                 |
  |------------|---------------------------------------------|
  | :char      | The character at postion col/row            |
  | :fg        | The foreground color of the character       |
  | :bg        | The background color of the character       |
  | :modifiers | A vector of SGR modifiers for the character |

  Returns nil if the character cannot be retrieved."
  [^TextGraphics tg col row]
  (let [^TextCharacter c (.getCharacter tg col row)]
    (when c
      {:char (utils/get-char c)
       :fg (utils/get-fg c)
       :bg (utils/get-bg c)
       :modifiers (utils/sgr-modifiers c)})))

(defn set-fg
  "Set the foreground color of the `TextGraphics` object to the color specified
  in the `color` argument. The `color` argument is either a string representing
  a color or a `TextColor` object. The string color value can be an ANSI color
  name e.g. 'white' or a 256 color index number e.g. `#17` or an RGB color
  name e.g. `#a4b2c3`."
  [^TextGraphics tg color]
  (.setForegroundColor tg ^TextColor (utils/make-color color)))

(defn set-bg
  "Set the background color of the `TextGraphics` object to the color specified
  in the `color` argument. The `color` argument is either a string representing
  a color or a `Textcolor` object. The string color value can be an ANSI color
  name e.g. 'black' or a 256 color index number e.g. '#20' or an RGB color
  specification string e.g. '#0a0b0c'."
  [^TextGraphics tg color]
  (.setBackgroundColor tg ^TextColor (utils/make-color color)))
