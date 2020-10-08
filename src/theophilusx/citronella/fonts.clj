(ns theophilusx.citronella.fonts
  (:import java.awt.GraphicsEnvironment
           java.awt.Font))

(defn get-available-fonts []
  (set (.getAvailableFontFamilyNames
        (GraphicsEnvironment/getLocalGraphicsEnvironment))))

(defn get-font-name [font]
  (let [fonts     (if (coll? font) font [font])
        fonts     (concat fonts ["Monospaced"])
        available (get-available-fonts)]
    (first (filter available fonts))))

