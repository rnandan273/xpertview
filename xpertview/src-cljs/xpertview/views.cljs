(ns xpertview.views
    (:require [re-frame.core :as re-frame]
              [re-com.core :as re-com]
              [reagent.core :as reagent :refer [atom]]
              [cljsjs.react-bootstrap :as react-bootstrap]))
;; home
(def ButtonInput (reagent/adapt-react-class js/ReactBootstrap.ButtonInput))
(def Input (reagent/adapt-react-class js/ReactBootstrap.Input))
(def Grid (reagent/adapt-react-class js/ReactBootstrap.Grid))
(def Row (reagent/adapt-react-class js/ReactBootstrap.Row))
(def Col (reagent/adapt-react-class js/ReactBootstrap.Col))

(defn log [s]
  (.log js/console (str s)))


(defn home-title []
  (let [name (re-frame/subscribe [:name])]
    (fn []
      [re-com/title
       :label (str "XpertView Toolbox")
       :underline? false
       :level :level1])))

(defn link-to-about-page []
  [re-com/hyperlink-href
   :label "go to About Page"
   :href "#/about"])

(defn clkhandler []
  (re-frame/dispatch [:set-search "text"]))

(defn chghandler []
  (re-frame/dispatch [:set-area "text"]))

(defn style-handler [style]
  (re-frame/dispatch [:set-search style]))

(defn area-handler [style])

(defn type-handler [style])

(defn dbsetup [style]
  (re-frame/dispatch [:dbsetup]))

(defn dbload [style]
  (re-frame/dispatch [:dbload]))

(defn dbpump [style]
  (re-frame/dispatch [:dbpump]))

(defn dbdown [style]
  (re-frame/dispatch [:dbdown]))


(defn button-comp []
  (fn []
    [re-com/button
     :label     "Search!"
     :on-click  #(clkhandler)
     :style     {:background-color "yellow"}]))

(defn textinput-comp []
  (fn []
    [:div [:h1 "text"]
     [re-com/input-text
      :model "string"
      :placeholder  "Enter Area in sqft!"
      :on-change #(chghandler)]]))

(defn home-panel-old []
  [re-com/v-box
   :gap "1em"
   :children [[home-title] [textinput-comp][button-comp][link-to-about-page]]])

(defn home-panel-old []
  [:div "Hello html"])


(defn products []
  (let [styles (re-frame/subscribe [:styles])]
   (fn []
     [Row
      (for [lx (into [] @styles)]
      [:h4 (str "Brand : " (get lx 0) " Category : " (get lx 1) " Details : " (get lx 2))])])))

(defn home-panel []
  [Grid
   [Col
    [Col
       [ButtonInput {:class "btn-material-light-blue-800" :type "submit" :bsSize "small"  :bsStyle "primary" :value "SetUp" :onClick #(dbsetup)}]
       [ButtonInput {:class "btn-material-light-blue-800" :type "submit" :bsSize "small"  :bsStyle "primary" :value "LoadSchema" :onClick #(dbload)}]
       [ButtonInput {:class "btn-material-light-blue-800" :type "submit" :bsSize "small"  :bsStyle "primary" :value "Pump Events" :onClick #(dbpump)}]]
       [ButtonInput {:class "btn-material-light-blue-800" :type "submit" :bsSize "small"  :bsStyle "primary" :value "Shutdown" :onClick #(dbdown)}]
   ]
   [Col
    [Col {:mdOffset 3 :xsOffset 6}
     [:h3 "XpertView Toolbox"]]]
   [Row
    [Col {:mdOffset 1 :xsOffset 4}
     [Input {:labelClassName "col-xs-2" :wrapperClassName "col-xs-6" :type "text"
             :bsSize "small" :label "Area in Square Feet ":placeholder "Input number eg 1250 "
             :onChange #(area-handler (-> % .-target .-value))}]]]
   [Row
    [Col {:mdOffset 1 :xsOffset 4}
     [Input {:labelClassName "col-xs-2" :wrapperClassName "col-xs-6" :type "text"
             :bsSize "small" :label "Type of residence ":placeholder "3BHK, 2BHK or villa"
             :onChange #(type-handler (-> % .-target .-value))}]]]
   [Row
    [Col {:mdOffset 1 :xsOffset 4}
     [Input {:labelClassName "col-xs-2" :wrapperClassName "col-xs-6" :type "text"
             :bsSize "small" :label "Quote for ":placeholder "interiors, painting, woodwork"
             :onChange #(style-handler (-> % .-target .-value))}]]]
   [Row
    [Col {:mdOffset 3 :xsOffset 6} [products]]]

   ])

;; about

(defn about-title []
  [re-com/title
   :label "This is the About Page."
   :level :level1])

(defn link-to-home-page []
  [re-com/hyperlink-href
   :label "go to Home Page"
   :href "#/"])

(defn about-panel []
  [re-com/v-box
   :gap "1em"
   :children [[about-title] [link-to-home-page]]])


;; main

(defmulti panels identity)
(defmethod panels :home-panel [] [home-panel])
(defmethod panels :about-panel [] [about-panel])
(defmethod panels :default [] [:div])
;(listview)
(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      [re-com/v-box
       :height "100%"
       :children [(panels @active-panel)]])))

(defn main-panel-old []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      [(panels @active-panel)])))
