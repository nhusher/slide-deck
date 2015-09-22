(ns slide-deck.slides
  (:require [clojure.zip :as z]))

(defmacro defslide [nm bindings body]
  `(defmethod slide-deck.slides/slide ~(keyword nm) [_# state# pos#]
     (let [~bindings [state#]]
       [(slide-deck.slides/section ~(name nm) state# pos# (sab/html ~body))
        ~(->> body flatten (filter symbol?) (map str) (filter #(= "s/on" %)) count)])))
