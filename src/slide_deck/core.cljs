(ns ^:figwheel-always slide-deck.core
  (:require [om.core :as om]
            [om.dom :as dom]
            [sablono.core :as sab]
            [cljs.core.async :refer [<! timeout]]
            [slide-deck.slides :as s])
  (:require-macros
    [slide-deck.slides :refer [defslide]]
    [cljs.core.async.macros :refer [go]]))

(enable-console-print!)

(def key-map {32 :advance
              39 :advance
              40 :advance
              38 :internal-back
              37 :back})

(def slide-list [:a-rant
                 :rather
                 :play-2
                 :demo
                 :questions?])

(defslide a-rant [state]
          [:div.vertical-center
           [:h2 "The world we live in"]
           [:h1 "A rant about web development"]
           [:ol
            (s/on 1 state [:li "Write code."])
            (s/on 2 state [:li "Wait for the code to be compiled/interpreted/whatever."])
            (s/on 3 state [:li "Reload the page."])
            (s/on 4 state [:li "Lose the application state."])
            (s/on 5 state [:li "Fiddle the app into the state you want it to be in."])
            (s/on 6 state [:li "Test your change."])
            (s/on 7 state [:li "Do it again."
                           (s/on 8 state [:span " And again."
                                          (s/on 9 state [:span " And again."])])])]])
(defslide rather [state]
          [:div.vertical-center
           [:h2 "I would rather"]
           [:h1 "Write code & instantly see the change"]])

(defslide play-2 [state]
          [:div.play-field
           [:img {:src "/img/oh.png" :style {:left "200px"}}]
           (s/only 0 state
                   [:div [:img {:style {:top "190px" :left "330px" :transform "scale(1,1) rotate(-25deg)"}
                                :src   "/img/ar-1.png"}]])
           (s/on 2 state
                 [:div
                  [:img {:style {:top "80px" :left "120px" :transform "rotate(20deg) scale(0.5,0.5)"} ; 220
                         :src   "/img/ar-1.png"}]
                  [:img {:style {:top "220px" :left "90px"} :src "/img/ex.png"}]]) ; 190
           (s/on 2 state
                 [:div
                  [:img {:style {:top "50px" :left "265px" :transform "rotate(-25deg) scale(0.5,0.5)"}
                         :src   "/img/ar-2.png"}]
                  [:img {:style {:top "90px" :left "385px"} :src "/img/oh.png"}]])
           (s/on 3 state
                 [:div
                  [:img {:style {:top "140px" :left "450px" :transform "rotate(-30deg) scale(0.5,0.5)"}
                         :src   "/img/ar-2.png"}]
                  [:img {:style {:top "160px" :left "575px"} :src "/img/ex.png"}]])
           (s/on 3 state
                 [:div
                  [:img {:style {:top "170px" :left "410px" :transform "rotate(10deg) scale(0.5,0.5)"}
                         :src   "/img/ar-1.png"}]
                  [:img {:style {:top "300px" :left "405px"} :src "/img/oh.png"}]])
           (s/on 4 state
                 [:div
                  [:img {:style {:top "390px" :left "435px" :transform "rotate(10deg) scale(-0.5,0.5)"}
                         :src   "/img/ar-1.png"}]
                  [:img {:style {:top "510px" :left "300px"} :src "/img/ex.png"}]])
           (s/on 4 state
                 [:div [:img {:style {:top "380px" :left "480px" :transform "rotate(-30deg) scale(0.5, 0.5)"}
                              :src   "/img/ar-1.png"}]])
           [:img {:style {:top "450px" :left "600px" :transform "scale(0.5,0.5)"} :src "/img/goal.png"}]])

(defslide demo [state]
          [:div.vertical-center
           [:h1 "Demo"]])

(defslide questions? [state]
          [:div.vertical-center
           [:h1 "Questions?"]])

(defonce app-state
         (atom {:counter     0
                :ins-counter 0
                :animate     true}))

(defn slide-deck [state _]
  (reify
    om/IRender
    (render [_]
      (sab/html [:div.slide-container
                 [:div.slide-plane
                  {:style {:transform (str "translate(-" (* 960 (:counter state)) "px, 0px)")}}
                  (map-indexed #(first (s/slide %2 state %1)) slide-list)]]))))






(defn subslide-count-for [sn]
  (second (s/slide (slide-list sn) nil nil)))

(defonce key-listener nil)

;; handle keyboard events
(defmulti dispatch identity)

(defn later [f] (js/setTimeout f 0))

(defn advance! []
  (om/transact! (om/root-cursor app-state) []
                (fn [{:keys [counter ins-counter] :as state}]
                  (if (<= (subslide-count-for counter) ins-counter)
                    (when (< counter (dec (count slide-list)))
                      (later #(om/update! (om/root-cursor app-state) [:ins-counter] 0))
                      (assoc state :counter (inc counter)))
                    (assoc state :ins-counter (inc ins-counter))))))

(defn back! []
  (om/transact! (om/root-cursor app-state)
                (fn [{:keys [counter] :as state}]
                  (assoc state :counter (min (max (dec counter) 0) (count slide-list))
                               :ins-counter 0))))

(defmethod dispatch :advance [_ data] (advance!))
(defmethod dispatch :internal-advance [_ data] (advance!))

(defmethod dispatch :internal-back [_ data]
  (om/transact! data []
                (fn [s]
                  (let [x (:ins-counter s)]
                    (assoc s :ins-counter
                             (if (zero? x) 0 (dec x)))))))

(defmethod dispatch :back [_ data]
  (later #(om/update! (om/root-cursor app-state) [:ins-counter] 0))
  (back!))

(defn dispatch! [action]
  (dispatch action (om/root-cursor app-state)))

(defn start! []
  (prn "Starting...")
  (when key-listener (js/document.body.removeEventListener "keyup" key-listener))
  (set! key-listener (fn [evt] (when-let [action (get key-map (.-keyCode evt))] (dispatch! action))))
  (js/document.body.addEventListener "keyup" key-listener)
  (om/root slide-deck app-state {:target (js/document.getElementById "app")}))


(defn on-js-reload [] (start!))
(start!)
