(ns scribe.log
  (:require [clojure.data.json :as json]
            [taoensso.carmine :as car :refer (wcar)]
            [scribe.redis :as redis]))

(use '[clojure.string :only (join trim)])

(defn- new-uuid []
  (.toString (java.util.UUID/randomUUID)))

(defn- scribe-key [uuid]
  (format "scribe:%s" uuid))

(defn- save [uuid]
  (redis/lpush (scribe-key uuid) ""))

(defn exists [uuid]
  (= 1 (redis/exists (scribe-key uuid))))

(defn all []
  (redis/keys "scribe:*"))

(defn build []
  (let [uuid (new-uuid)]
    (save uuid)
    uuid))

(defn destroy [uuid]
  (redis/del (scribe-key uuid)))

(defn append-message [uuid message]
  (redis/rpush (scribe-key uuid) message))

(defn messages [uuid]
  (redis/lrange (scribe-key uuid)))

(defn as-json [uuid]
  (if (redis/exists (scribe-key uuid))
    (json/write-str {:uuid uuid 
                     :_links [{:self {:href (format "/logs/%s" uuid)}} 
                              {:messages {:href (format "/logs/%s/messages" uuid)}}]})
    (json/write-str {})))

(defn as-text [uuid]
  (if (redis/exists (scribe-key uuid))
    (trim (join "\n" (messages uuid)))
    ""))

(defn as [format uuid]
  (case format
    "application/json" (as-json uuid)
    "text/plain" (as-text uuid)))

