(ns theophilusx.citronella.utils
  (:import [com.googlecode.lanterna TextColor$Factory TextCharacter
            TerminalPosition TerminalSize SGR])
  (:require [clojure.string :as string]
            [theophilusx.citronella.constants :as c]))

(defn make-color [s]
  (if (string? s)
    (TextColor$Factory/fromString s)
    s))

(defn make-character
  ([chr]
   (TextCharacter. chr))
  ([chr fg bg]
   (TextCharacter. chr (make-color fg)  (make-color bg) (make-array SGR 0)))
  ([chr fg bg sgr-vec]
   (let [ar (into-array SGR (map #(% c/sgr) sgr-vec))]
     (TextCharacter. chr (make-color fg) (make-color bg) ar))))

(defn get-fg [^TextCharacter chr]
  (.toString (.getForegroundColor chr)))

(defn get-bg [^TextCharacter chr]
  (.toString (.getBackgroundColor chr)))

(defn get-char [^TextCharacter chr]
  (.getCharacter chr))

(defn sgr-modifiers [^TextCharacter chr]
  (let [mods (.getModifiers chr)]
    (vec (for [m mods]
           (keyword (string/lower-case (.toString m)))))))

(defn column-pos [^TerminalPosition p]
  (.getColumn p))

(defn row-pos [^TerminalPosition p]
  (.getRow p))

(defn columns [^TerminalSize s]
  (.getColumns s))

(defn rows [^TerminalSize s]
  (.getRows s))
