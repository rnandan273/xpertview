(ns xpertview.core
  (:require [reagent.core :as r]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [re-frame.core :as re-frame]
            [xpertview.routes :as routes]
            [xpertview.views :as views]
            [xpertview.subs]
            [xpertview.handlers]
            [ajax.core :refer [GET POST]])
  (:import goog.History))


;; -------------------------
;; Initialize app
(defn fetch-docs! []
  (GET (str js/context "/docs") {:handler #(session/put! :docs %)}))


(defn mount-root []
  (r/render [views/main-panel]
                  (.getElementById js/document "app")))

(defn init! []
  (fetch-docs!)
  ;(hook-browser-navigation!)

  (re-frame/dispatch-sync [:initialize-db])
  (mount-root)
  (routes/app-routes))
