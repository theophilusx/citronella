(ns theophilusx.citronella.utils
  (:import [com.googlecode.lanterna TextColor TextColor$Factory TextCharacter
            TerminalPosition TerminalSize SGR])
  (:require [theophilusx.citronella.constants :as c]
            [clojure.string :as string]))

(defn make-color [s]
  (if (string? s)
    (TextColor$Factory/fromString s)
    s))

(defn make-character
  ([chr]
   (TextCharacter. chr))
  ([chr fg bg]
   (TextCharacter. chr (make-color fg)  (make-color bg) (make-array SGR 0))))

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
  (.getRows s))
