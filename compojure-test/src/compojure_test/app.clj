(ns compojure-test.app
  "Compojure test with embedded server, no magic."
  (:use compojure.core
        ring.adapter.jetty)
  (:require [compojure-test.handler :as h])
  (:import org.eclipse.jetty.server.Server))

(set! *warn-on-reflection* true)

; Global server reference.  Starts off nil until the server is started.  Set
; back to nil when server stopped.
(defonce server (ref nil))

(defn config-server
  "Access to the server instance before it is started.  Returns nil."
  [^Server s]
  (.setStopAtShutdown s true))

(defn start-server 
  "Start the embedded web server if not running.  Does nothing if server
  already running.
  Usage: (start-server)            <-- good for repl
         (start-server :join true) <-- good for main"
  [& {:keys [join] :or {join false}}]
  (dosync
    (when-not (ensure server)
      (ref-set server (run-jetty #'h/dummy-handler 
                                 {:port 3000 
                                  :join? join
                                  :configurator config-server})))))

(defn stop-server 
  "Initiate orderly shutdown of the server.  Blocks until done.  If already
  shutdown, does nothing."
  []
  (dosync
    (when-let [^Server s (ensure server)]
      (.stop s)
      (ref-set server nil))))

(defn -main [& args]
  ; start the server and block forever
  (start-server :join true)
  (println "bye"))

