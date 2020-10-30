(ns citronella-demo.demo2
  (:require [theophilusx.citronella.screen :as screen]
            [theophilusx.citronella.utils :as utils])
  (:import com.googlecode.lanterna.TextColor$ANSI))

(defn random-color []
  (let [colors (TextColor$ANSI/values)]
    (nth colors (rand-int (count colors)))))

(defn -main []
  (let [scrn (screen/get-screen {:type :gui})]
    (screen/start scrn)
    (doseq [c (range (first (screen/size scrn)))
            r (range (second (screen/size scrn)))]
      (screen/put-char
        scrn c r
        (utils/make-character \space
                              (utils/make-color "default")
                              (utils/make-color (random-color)))))
    (screen/refresh scrn)
    (loop [ks (screen/read-input scrn)]
      (if (= (:type ks) :escape)
        (screen/close scrn)
        (recur (screen/read-input scrn))))))
