(ns my-project.websocket
  (:require
   [ring.adapter.jetty9.websocket :as jetty-ws]
   [ring.websocket :as ring-ws]))

(defn on-open [ws]
  ;; you probably want to track open connections here
  )

(defn on-close [ws status-code reason]
  )

(defn on-error [ws e]
  (println "Websocket error" e)
  (set! *e e))

(defn on-message [ws message]
  (println "Websocket message" message))

(defn on-ping [ws bytebuffer]
  ;; not sure this is really necessary
  (ring-ws/pong ws))

(defn on-pong [ws bytebuffer])

(def handlers
  {:on-open #'on-open
   :on-error #'on-error
   :on-close #'on-close
   :on-message #'on-message
   :on-ping #'on-ping})

(defn GET-upgrade [req]
  (when (jetty-ws/ws-upgrade-request? req)
    {:ring.websocket/listener
     handlers}))
