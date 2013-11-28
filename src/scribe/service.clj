(ns scribe.service
    (:require [io.pedestal.service.http :as bootstrap]
              [io.pedestal.service.http.route :as route]
              [io.pedestal.service.http.body-params :as body-params]
              [io.pedestal.service.http.route.definition :refer [defroutes]]
              [io.pedestal.service.interceptor :as interceptor]
              [ring.util.response :as ring-resp]
              [clojure.data.json :as json]
              [scribe.log]))

(interceptor/defon-response json-content-type [response]
  (ring-resp/content-type response "application/json"))

;; Logs

(defn logs-index [request]
    (ring-resp/response (json/write-str (scribe.log/all))))

(defn logs-show [request]
  (let [uuid (-> request :path-params :id)
        format (-> request :headers :accept)
        response (if (scribe.log/exists uuid) 
                     (scribe.log/as format uuid)
                     {:error "Log does not exist"})]
    (ring-resp/response response)))

(defn logs-create [request]
  (let [uuid (scribe.log/build)
        log (scribe.log/as-json uuid)]
    (ring-resp/status (ring-resp/response (json/write-str log)) 201)))

(defn logs-destroy [request]
  (let [uuid (-> request :path-params :id)]
    (scribe.log/destroy uuid))
  (ring-resp/status (ring-resp/response nil) 204))

;; Messages

(defn messages-index [request]
  (let [uuid (-> request :path-params :id)
        messages (scribe.log/messages uuid)]
    (ring-resp/response (json/write-str messages))))

(defn messages-create [request]
  (let [uuid (-> request :path-params :id)
        message (-> request :json-params :content)]
    (scribe.log/append-message uuid message))
  (ring-resp/status (ring-resp/response nil) 201))

;; Routes

(defroutes routes
  [[["/logs" {:get logs-index :post logs-create}
     ^:interceptors [body-params/body-params json-content-type]
     ["/:id" {:get logs-show :delete logs-destroy}]
     ["/:id/messages" {:get messages-index :post messages-create}]]]])

(def service {:env :prod
              ::bootstrap/routes routes
              ::bootstrap/resource-path "/public"
              ::bootstrap/type :jetty
              ::bootstrap/port 8080})
