(ns scribe.redis
  (:require [taoensso.carmine :as car :refer (wcar)]))

(def redis-conn
  {:pool {} :spec {}})

(defmacro wcar* [& body]
  `(car/wcar redis-conn ~@body))

(defn keys 
  ([] (keys "*"))
  ([pattern] (wcar* (car/keys pattern))))

(defn exists [key]
  (wcar* (car/exists key)))

(defn del [key]
  (wcar* (car/del key)))

(defn lpush [key value]
  (wcar* (car/lpush key value)))

(defn rpush [key value]
  (wcar* (car/rpush key value)))

(defn lrange
  ([key] (lrange key 0 -1))
  ([key start] (lrange key start -1))
  ([key start end] (wcar* (car/lrange key start end))))

