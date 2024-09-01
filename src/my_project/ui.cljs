(ns my-project.ui)

(js/console.log "ui is here")

(def ws (js/WebSocket. (str "ws://" js/document.location.host "/socket" )))


(set! (.-innerHTML (js/document.getElementById "app"))
      "<button id=action-button>Send</button><br><div id=response></div>")

(.addEventListener (js/document.getElementById "action-button")
                   "click"
                   #(.send ws "hello"))

(set! (.-onmessage ws)
      (fn [message-event]
        (def *m message-event)
        (println "response:" (.-data *m))
        (set! (.-innerHTML (js/document.getElementById "response"))
              (.-data *m))))

