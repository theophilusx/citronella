(ns theophilusx.citronella.text-graphics
  (:import com.googlecode.lanterna.graphics.TextGraphics
           [com.googlecode.lanterna TerminalPosition TerminalSize
            TextCharacter])
  (:require [theophilusx.citronella.constants :as c]))

(defn put-string
  ([tg s col row]
   (.putString tg col row s))
  ([tg s col row sgr-vec]
   (let [sgr-opts (mapv #(% c/sgr) sgr-vec)]
     (.putString tg col row s ^java.util.Collection sgr-opts))))

(defn set-foreground [^TextGraphics tg colour]
  (.setForeground tg (colour c/ansi)))

(defn set-background [^TextGraphics tg colour]
  (.setBackground tg (colour c/ansi)))

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
      (.getCharacter c))))
