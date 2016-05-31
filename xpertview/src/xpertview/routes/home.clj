(ns xpertview.routes.home
  (:require [xpertview.layout :as layout]
            [compojure.core :refer [defroutes GET]]
            [ring.util.http-response :refer [ok]]
            [clojure.tools.logging :as log]
            [clojure.java.io :as io]
            [xpertview.interiors :as ia]))

(defn home-page []
  (layout/render "home.html"))

(defroutes home-routes
  (GET "/" [] (home-page))
  (GET "/docs" [] (ok (-> "docs/docs.md" io/resource slurp)))

  (GET "/dbsetup" [] (ok (ia/dbsetup)))
  (GET "/dbload" [] (ok (ia/dbload)))
  (GET "/dbdown" [] (ok (ia/dbshutdown)))
  (GET "/dbpump" [] (ok (ia/dbpump)))
  (GET "/styles" req
         (let [query (:query (:params req))]
         (log/info "Query Param is -> " query)

         (def json_resp {:answers (ia/query-paint query)})
         (log/info (into {} json_resp))

         (ok (into {} json_resp))))
)

