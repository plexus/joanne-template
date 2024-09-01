(ns my-project.main
  (:require
   [lambdaisland.hiccup :as hiccup]
   [reitit.ring :as ring]
   [reitit.ring.middleware.exception :as exception]
   [ring.adapter.jetty9 :as jetty]
   [ring.middleware.resource :as ring-resource]
   [ring.websocket :as ring-ws]
   [my-project.websocket :as ws]))

(defn GET-index [req]
  {:status 200
   :headers {"content-type" "text/html"}
   :body (hiccup/render [:html
                         [:head
                          [:script {:src "/ui/main.js"}]]
                         [:body
                          [:h1 "boop"]
                          [:div#app]]])})

(defn routes []
  [["/" {:get {:handler #'GET-index}}]
   ["/socket" {:get {:handler #'ws/GET-upgrade}}]])

(defn fallback-handler []
  (ring-resource/wrap-resource
   (ring/routes ; fallback handler
    (ring/redirect-trailing-slash-handler {:method :strip})
    (ring/create-default-handler))
   "public"))

(defn ring-handler []
  (ring/ring-handler
   (ring/router (routes))
   (fallback-handler)
   {:middleware [;; vvvvvvv  Request goes down
                 ;; [ring-defaults/wrap-defaults ring-default-config]
                 ;; ^^^^^^^  Response goes up
                 ]}))

(defn start-http []
  (jetty/run-jetty
   (fn [req]
     ((ring-handler) req))
   {:port 8000
    :join? false
    ;; Default idle timeout for jetty is 5 minutes, you're supposed to implement
    ;; reconnect behavior on the client, setting it to 1 hour to ignore it for
    ;; now
    :ws-max-idle-time (* 60 60 1000)}))

(defonce jetty (start-http))

(comment
  (do
    (.stop jetty)
    (alter-var-root #'jetty (constantly (start-http)))))
