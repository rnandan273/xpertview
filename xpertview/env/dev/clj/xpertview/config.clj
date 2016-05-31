(ns xpertview.config
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [xpertview.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[xpertview started successfully using the development profile]=-"))
   :middleware wrap-dev})
