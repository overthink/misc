(ns compojure-test.handler)

(defn dummy-handler 
  "Trying out a ring handler."
  [req]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body "Hello World - yo"})

