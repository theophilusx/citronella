(ns theophilusx.citronella.text-graphics
  (:import com.googlecode.lanterna.graphics.TextGraphics
           [com.googlecode.lanterna TerminalPosition TerminalSize
            TextCharacter])
  (:require [theophilusx.citronella.constants :as c]
            [theophilusx.citronella.utils :as utils]))

(defn put-string
  ([tg s col row]
   (.putString tg col row s))
  ([tg s col row sgr-vec]
   (let [sgr-opts (mapv #(% c/sgr) sgr-vec)]
     (.putString tg col row s ^java.util.Collection sgr-opts))))

(defn set-foreground [^TextGraphics tg color]
  (.setForeground tg (utils/make-color color)))

(defn set-background [^TextGraphics tg color]
  (.setBackground tg (utils/make-color color)))

(defn draw-line [^TextGraphics tg colx rowx coly rowy chr]
  (.drawLine tg colx rowx coly rowy chr))

(defn draw-rectangle [^TextGraphics tg col row cols rows chr]
  (let [pos (TerminalPosition. col row)
        size (TerminalSize. cols rows)]
    (.drawRectangle tg pos size chr)))

(defn fill [^TextGraphics tg chr]
  (.fill tg chr))

(defn fill-rectangle [^TextGraphics tg col row cols rows chr]
  (let [pos (TerminalPosition. col row)
        size (TerminalSize. cols rows)]
    (.fillRectangle tg pos size chr)))

(defn get-char [^TextGraphics tg col row]
  (let [^TextCharacter c (.getCharacter tg col row)]
    (when c
      {:char (utils/get-char c)
       :fg (utils/get-fg c)
       :bg (utils/get-bg c)
       :modifiers (utils/sgr-modifiers c)})))
