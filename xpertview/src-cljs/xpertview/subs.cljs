(ns xpertview.subs
    (:require-macros [reagent.ratom :refer [reaction]])
    (:require [re-frame.core :as re-frame]))

(defn log [s]
  (.log js/console (str s)))

(re-frame/register-sub
 :name
 (fn [db]
   (reaction (:name @db))))

(re-frame/register-sub
 :active-panel
 (fn [db _]
   (reaction (:active-panel @db))))

(re-frame/register-sub
 :srch-str
 (fn [db _]
   (log "In subscriber")
   (reaction (:srch-str @db))))


(re-frame/register-sub
 :srch-area
 (fn [db _]
   (log "In subscriber")
   (reaction (:srch-area @db))))

(re-frame/register-sub
 :styles
 (fn [db]
   (log "In style subscriber" (first (:styles @db)))
   (reaction (:styles @db))))
