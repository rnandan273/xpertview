(ns xpertview.handlers
    (:require [re-frame.core :as re-frame]
              [xpertview.db :as db]
              [cljs.core.async :as async :refer [chan close!]]
              [clojure.walk :as walk]
              [cognitect.transit :as t]
              [ajax.core :refer [GET POST]])
  (:require-macros
    [cljs.core.async.macros :refer [go alt!]]))

(defn log [s]
  (.log js/console (str s)))

(re-frame/register-handler
 :initialize-db
 (fn  [_ _]
   db/default-db))

(re-frame/register-handler
 :set-active-panel
 (fn [db [_ active-panel]]
   (log "Active panel" active-panel)
   (assoc db :active-panel active-panel)))

(defn read-style-response [response]
  (let [kwresp (walk/keywordize-keys response)]
    (log  (into () (:answers kwresp)))
    (re-frame/dispatch [:styles (into () (:answers kwresp))])))

(defn response-handler [ch response]
  (go (>! ch response)(close! ch))
  (log "DONE"))

(defn read-setup-response [response]
  (log "DONE"))

(defn do-http-get [url]
  (log (str "GET " url))
  (let [ch (chan 1)]
    (GET url {:handler (fn [response](response-handler ch response))
              :error-handler (fn [response] (response-handler ch response))})
    ch))

(re-frame/register-handler
 :set-search
 (fn [db [_ srch-str]]
   (log "In handler" srch-str)
  (def service_url (str "/styles?query=" srch-str))
  (log service_url)
  (go
     (read-style-response (<! (do-http-get service_url))))
     (assoc db :active-panel :home-panel)))

(re-frame/register-handler
 :styles
 (fn [db [_ styleres]]
   (log "In style handler" styleres)
   (assoc db :styles styleres)))

(re-frame/register-handler
 :dbsetup
 (fn [db [_ _]]
  (def service_url (str "/dbsetup"))
  (log service_url)
  (go
     (read-setup-response (<! (do-http-get service_url))))
     (assoc db :active-panel :home-panel))
 )

(re-frame/register-handler
 :dbload
 (fn [db [_ _]]
  (def service_url (str "/dbload"))
  (log service_url)
  (go
     (read-setup-response (<! (do-http-get service_url))))
   (assoc db :active-panel :home-panel))
 )

(re-frame/register-handler
 :dbpump
 (fn [db [_ _]]
  (def service_url (str "/dbpump"))
  (log service_url)
  (go
     (read-setup-response (<! (do-http-get service_url))))
   (assoc db :active-panel :home-panel)))

(re-frame/register-handler
 :dbdown
 (fn [db [_ _]]
  (def service_url (str "/dbdown"))
  (log service_url)
  (go
     (read-setup-response (<! (do-http-get service_url))))
   (assoc db :active-panel :home-panel)))

(re-frame/register-handler
 :set-area
 (fn [db [_ srch-area]]
   (log "In handler")
   (assoc db :srch-area srch-area)))
