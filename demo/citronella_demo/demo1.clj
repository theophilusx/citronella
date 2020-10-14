(ns citronella-demo.demo1
  (:require [theophilusx.citronella.terminal :as t]
            [theophilusx.citronella.constants :as c]))

(defn -main []
  (let [term (t/get-terminal {:type :gui})]
    (t/toggle-private-mode term)
    (t/toggle-cursor-visible term)
    (t/set-tg-foreground term (:white c/ansi))
    (t/set-tg-background term (:black c/ansi))
    (t/put-string term "Citronella Demo 1 - Press ESC to exit" 2 1)
    (t/set-tg-foreground term (:default c/ansi))
    (t/set-tg-background term (:default c/ansi))
    (t/put-string term "Terminal Size: " 5 3 [:bold])
    (t/put-string term (str (:size @term)) (+ 5 (count "Terminal Size: ")) 3)
    (t/flush-data term)
    (t/put-string term "Last Keystroke: " 5 4 [:bold])
    (t/put-string term "<pending>" (+ 5 (count "Last Keystroke: ")) 4)
    (t/draw-line term 5 6 (- (first (:size @term)) 5) 6 (:double-line-h c/symbols))
    (t/flush-data term)
    (loop [ks (t/read-input term)]
      (t/put-string term (str (:type ks) " " (:char ks)) (+ 5 (count "Last Keystroke: ")) 4)
      (t/flush-data term)
      (if (= (:type ks) :escape)
        (t/close term)
        (recur (t/read-input term)))))) 



