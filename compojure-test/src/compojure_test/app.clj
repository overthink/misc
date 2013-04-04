(ns compojure-test.app
  "Compojure test with embedded server, no magic."
  (:use compojure.core
        ring.adapter.jetty)
  (:require [compojure-test.handler :as h])
  (:import org.eclipse.jetty.server.Server))

(set! *warn-on-reflection* true)

(defonce server (atom nil))

(defn config-jetty
  "Access to the server instance before it is started."
  [^Server s]
  (.setStopAtShutdown s true))

(defn start-server 
  "Useful when working in REPL."
  []
  ; Should check that it's not started
  ; Should take join value as arg
  (swap! server (fn [_] (run-jetty #'h/dummy-handler 
                                   {:port 3000 
                                    :join? false
                                    :configurator config-jetty}))))

(defn stop-server []
  (let [^Server s @server]
    (.stop s)))


(defn -main [& args]
  (start-server)
  (.join @server)
  (println "bye"))

