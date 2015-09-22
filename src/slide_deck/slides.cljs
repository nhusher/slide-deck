(ns slide-deck.slides
  (:require [sablono.core :as sab]))

(defmulti slide (fn [a] a))

(defmethod slide :default [s]
  (sab/html [:div "empty"]))

(defn scrub-slide-name [ nm ]
  (clojure.string/replace nm #"[^a-zA-Z-]" ""))

(defn section [nm state pos content]
  (sab/html
    [:section.slide
     {:style {:left (str (* pos 960)  "px")}
      :key nm
      :class (str (if (= pos (:counter state)) "active" "inactive") " slide-name-" (scrub-slide-name (name nm)))}
     content]))

(defn on [num state content]
  (apply vector (first content)
         (if (map? (second content))
           (merge (second content)
                  { :className (if (>= (:ins-counter state) num) "inner-sect visible" "inner-sect hidden") })
           { :className (if (>= (:ins-counter state) num) "inner-sect visible" "inner-sect hidden")})
         (if (map? (second content)) (nthrest 2 content) (rest content))))

(defn only [num state content]
  (apply vector (first content)
         (if (map? (second content))
           (merge (second content)
                  { :className (if (= (:ins-counter state) num) "inner-sect visible" "inner-sect hidden") })
           { :className (if (= (:ins-counter state) num) "inner-sect visible" "inner-sect hidden")})
         (if (map? (second content)) (nthrest 2 content) (rest content))))
