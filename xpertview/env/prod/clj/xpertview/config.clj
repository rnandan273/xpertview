(ns xpertview.config
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[xpertview started successfully]=-"))
   :middleware identity})
