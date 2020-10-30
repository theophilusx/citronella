(ns theophilusx.citronella.utils
  (:import [com.googlecode.lanterna TextColor TextColor$Factory TextCharacter
            TerminalPosition TerminalSize])
  (:require [theophilusx.citronella.constants :as c]))

(defn make-color [s]
  (TextColor$Factory/fromString s))

(defn make-character
  ([chr]
   (TextCharacter. chr))
  ([chr fg bg & sgr-vec]
   (let [sgr-opts (mapv #(% c/sgr) (first sgr-vec))]
     (TextCharacter. chr ^TextColor (make-color fg) ^TextColor (make-color bg)
                     ^java.util.Colleciton sgr-opts))))

(defn get-fg [^TextCharacter chr]
  (.getForegroundColor chr))

(defn get-bg [^TextCharacter chr]
  (.getBackgroundColor chr))

(defn get-char [^TextCharacter chr]
  (.getCharacter chr))

(defn sgr-modifiers [^TextCharacter chr]
  (.getModifiers chr))

(defn column-pos [^TerminalPosition p]
  (.getColumn p))

(defn row-pos [^TerminalPosition p]
  (.getRow p))

(defn columns [^TerminalSize s]
  (.getColumns s))

(defn rows [^TerminalSize s]
  (:.getRows s))
