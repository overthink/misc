(ns pedestal-test.service
    (:require [io.pedestal.service.http :as bootstrap]
              [io.pedestal.service.http.route :as route]
              [io.pedestal.service.http.body-params :as body-params]
              [io.pedestal.service.http.route.definition :refer [defroutes]]
              [io.pedestal.service.http.sse :as sse]
              [io.pedestal.service.interceptor :as interceptor]
              [io.pedestal.service.interceptor :as interceptor]
              [io.pedestal.service.impl.interceptor :as interceptor-impl]
              [ring.util.response :as ring-resp]))

(defn about-page
  [request]
  (ring-resp/response (format "Clojure %s\n" (clojure-version))))

(defn home-page
  [request]
  (ring-resp/response "Hello World!\n"))

(defn home-page2
  [request]
  (ring-resp/response "Hello World2!\n"))

(defn cur-thread [] (. (Thread/currentThread) (getName)))

(defn wait [req start-thread]
  (Thread/sleep (* 5 1000))
  (format "done waiting, start: %s, end: %s"
          start-thread
          (cur-thread)))

(interceptor/defhandler takes-time [req]
  (ring-resp/response (wait req (cur-thread))))

(interceptor/defbefore non-blocking-wait [{req :request :as context}]
  (interceptor-impl/with-pause [paused-context context]
    (let [start-thread (cur-thread)]
      (future
        (let [result (wait req start-thread)]
          (interceptor-impl/resume
            (assoc paused-context :response (ring-resp/response result))))))))

;; -- little streaming event test --

(def event-stream-ctx (atom nil)) ;; real impl would be keyed by session/user

(defn clean-up []
  (when-let [streaming-ctx @event-stream-ctx]
    (reset! event-stream-ctx nil)
    (sse/end-event-stream streaming-ctx)))

(defn notify [event-name event-data]
  (when-let [streaming-ctx @event-stream-ctx]
    (try
      (sse/send-event streaming-ctx event-name event-data)
    (catch java.io.IOException e
      (clean-up)))))

;; -- end streaming event stuff --

(defroutes routes
  [[:pedestal-test-app
    ["/"
     ^:interceptors [(body-params/body-params) bootstrap/html-body] ;; applies throughout, incl "/"
     {:get home-page}
     ["/about" {:get about-page}]
     ["/events" {:get [::events (sse/start-event-stream #(reset! event-stream-ctx  %1))]}]
     ["/wait" {:get takes-time}]
     ["/wait-nb" {:get non-blocking-wait}]]]
   [:otherapp
    ["/" {:get home-page2}]]])

;; You can use this fn or a per-request fn via io.pedestal.service.http.route/url-for
(def url-for (route/url-for-routes routes))

;; Consumed by pedestal-test.server/create-server
(def service {:env :prod
              ;; You can bring your own non-default interceptors. Make
              ;; sure you include routing and set it up right for
              ;; dev-mode. If you do, many other keys for configuring
              ;; default interceptors will be ignored.
              ;; :bootstrap/interceptors []
              ::bootstrap/routes routes

              ;; Uncomment next line to enable CORS support, add
              ;; string(s) specifying scheme, host and port for
              ;; allowed source(s):
              ;;
              ;; "http://localhost:8080"
              ;;
              ;;::boostrap/allowed-origins ["scheme://host:port"]

              ;; Root for resource interceptor that is available by default.
              ::bootstrap/resource-path "/public"

              ;;::bootstrap/host "localhost"
              ::bootstrap/type :jetty
              ::bootstrap/port 9999})
