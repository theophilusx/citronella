(ns theophilusx.citronella.terminal
  (:require [theophilusx.citronella.constants :as c])
  (:import com.googlecode.lanterna.terminal.DefaultTerminalFactory
           com.googlecode.lanterna.terminal.TerminalResizeListener
           java.nio.charset.Charset))

(defn get-terminal
  ([]
   (get-terminal {}))
  ([{:keys [type out in charset]
     :or   {type    :auto
            out     System/out
            in      System/in
            charset "UTF-8"}}]
   (let [factory (DefaultTerminalFactory. out in (Charset/forName charset))
         term    (case type
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
                              [(.getColumn pos) (.getRow pos)])
            :cursor-visible true
            :size           (let [size (.getTerminalSize term)]
                              [(.getColumns size) (.getRows size)])
            :text-graphics  (.newTextGraphics term)
            :obj            term}))))

(defn bell [term]
  (.bell (:obj @term)))

(defn cursor-position [term]
  (let [pos (.getCursorPosition (:obj @term))]
    (swap! term assoc :cursor [(.getColumn pos) (.getRow pos)])
    (:cursor @term)))

(defn set-cursor [term col row]
  (.setCursorPosition (:obj @term) col row)
  (cursor-position term))

(defn set-background [term colour]
  (.setBackgroundColor (:obj @term) colour))

(defn set-foreground [term colour]
  (.setForegroundColor (:obj @term) colour))

(defn toggle-cursor-visible [term]
  (.setCursorVisible (:obj @term) (not (:cursor-visible @term)))
  (swap! term update :cursor-visible not))

(defn terminal-size [term]
  (let [size (.getTerminalSize (:obj @term))]
    (swap! term assoc :size [(.getColumns size) (.getRows size)])
    (:size @term)))

(defn clear [term]
  (.clearScreen (:obj @term))
  (cursor-position term))

(defn toggle-private-mode [term]
  (if (:private @term)
    (.exitPrivateMode (:obj @term))
    (.enterPrivateMode (:obj @term)))
  (swap! term update :private not))

(defn close [term]
  (when (:private @term)
    (toggle-private-mode term))
  (.close (:obj @term))
  (swap! term assoc :open false))


(defn flush-data [term]
  (.flush (:obj @term))
  (cursor-position term))

(defn put-char [term c]
  (.putCharacter (:obj @term) c)
  (cursor-position term))

(defn put-string
  ([term s]
   (let [[col row] (:cursor @term)]
     (put-string term s col row)))
  ([term s col row]
   (.putString (:text-graphics @term) col row s))
  ([term s col row sgr]
   (.putString (:text-graphics @term) col row s [sgr])))

(defn set-tg-foreground [term colour]
  (.setForegroundColor (:text-graphics @term) colour))

(defn set-tg-background [term colour]
  (.setBackgroundColor (:text-graphics @term) colour))

(defn read-input [term]
  (let [ks (.readInput (:obj @term))]
    {:event-time (.getEventTime ks)
     :type (c/key-code->name (.getKeyType ks))
     :alt (.isAltDown ks)
     :control (.isCtrlDown ks)
     :shift (.isShiftDown ks)
     :char (.getCharacter ks)}))

(defn add-resize-listener [term listener-fn]
  (let [listener (reify TerminalResizeListener
                   (onResized [this terminal newSize]
                     (listener-fn (.getColumns newSize)
                                  (.getRows newSize))))]
    (.addResizeListener (:obj @term) listener)
    (swap! term assoc :listener listener)
    listener))

(defn remove-resize-listener [term]
  (when (:listener @term)
    (.removeResizeListener (:obj @term) (:listener @term))
    (swap! term assoc :listener nil)))
