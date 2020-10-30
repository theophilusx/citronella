(ns theophilusx.citronella.screen
  (:require [theophilusx.citronella.terminal :as terminal]
            [theophilusx.citronella.utils :as utils])
  (:import [com.googlecode.lanterna.screen Screen TerminalScreen]))

(defn get-screen
  ([]
   (get-screen {}))
  ([opts]
   (let [term         (terminal/get-terminal opts)
         ^Screen scrn (TerminalScreen. (:obj @term))]
     (atom {:type   (:type @term)
            :started false
            :cursor (let [pos (.getCursorPosition scrn)]
                      [(utils/column-pos pos) (utils/row-pos pos)])
            :size   (let [size (.getTerminalSize scrn)]
                      [(utils/columns size) (utils/rows size)])
            :text-graphics (.newTextGraphics scrn)
            :obj    scrn}))))

(defn start [scrn]
  (when (not (:started @scrn))
    (.startScreen (:obj @scrn))
    (swap! scrn update :started not)))

(defn stop [scrn]
  (when (:started @scrn)
    (.stopScreen (:obj @scrn))
    (swap! scrn update :started not)))

(defn cursor-position [scrn]
  (let [pos (.getCursorPosition (:obj @scrn))]
    (swap! scrn assoc :cursor [(utils/column-pos pos) (utils/row-pos pos)])
    (:curesor @scrn)))

(defn set-cursor [scrn col row]
  (.setCursorPosition (:obj @scrn) col row))

(defn size [scrn]
  (let [size (.getTerminalSize (:obj @scrn))]
    (swap! scrn assoc :size [(utils/columns size) (utils/rows size)])
    (:size @scrn)))

(defn refresh [scrn]
  (.refresh (:obj @scrn)))

(defn clear [scrn]
  (.clear (:obj @scrn)))

(defn get-back-char [scrn col row]
  (let [c (.getBackCharacter (:obj @scrn) col row)]
    (when c
      {:char (utils/get-char c)
       :fg (utils/get-fg c)
       :bg (utils/get-bg c)
       :modifiers (utils/sgr-modifiers c)})))

(defn get-front-char [scrn col row]
  (let [c (.getFrontCharacter (:obj @scrn) col row)]
    (when c
      {:char (utils/get-char c)
       :fg (utils/get-fg c)
       :bg (utils/get-bg c)
       :modifiers (utils/sgr-modifiers c)})))

(defn put-char [scrn col row chr]
  (let [tc (utils/make-character chr)]
    (.setCharacter (:obj @scrn) col row tc)))
