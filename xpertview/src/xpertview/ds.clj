(ns xpertview.ds
  (:require [datomic.api :as d]
            [datomic-schema.schema :as s]
            [clojure.tools.logging :as log])
  (:gen-class)
  (:import java.util.Date))

(defn testInteriors []
  (log/info "Reached Interiors"))

(comment 

(defonce db-url "datomic:mem://testdb")

(defdbfn dbinc [db e a qty] :db.part/user
  [[:db/add e a (+ qty (or (get (d/entity db e) a) 0))]])

(defn dbparts []
  [(part "app")])

(defn dbschema []
  [(schema user
    (fields
     [username :string :indexed]
     [pwd :string "Hashed password string"]
     [email :string :indexed]
     [status :enum [:pending :active :inactive :cancelled]]
     [group :ref :many]))

   (schema group
    (fields
     [name :string]
     [permission :string :many]))])

(defn setup-db [url]
  (d/create-database url)
  (d/transact
   (d/connect url)
   (concat
    (s/generate-parts (dbparts))
    (s/generate-schema (dbschema))
    (s/dbfns->datomic dbinc))))

(defn test-db [& args]
  (setup-db db-url)
  (let [gid (d/tempid :db.part/user)]
    (d/transact
     db-url
     [{:db/id gid
       :group/name "Staff"
       :group/permission "Admin"}
      {:db/id (d/tempid :db.part/user)
       :user/username "bob"
       :user/email "bob@example.com"
       :user/group gid
       :user/status :user.status/pending}])))
)

