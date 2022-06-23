(ns comp.click
  (:require
   [comp.props :refer [props]]))

(defn click-af [{:as env :keys [on-click mk-on-click] env-props :props}]
  (when (or mk-on-click on-click)
    {:props (assoc env-props
                   :on-click (cond mk-on-click
                                   (mk-on-click env)
                                   on-click
                                   #(on-click %)
                                   :else
                                   identity))}))

(defn click-ef [{:as env {:as env-props :keys [on-click]} :props}]
  (when on-click
    {:props (assoc env-props :on-click #(on-click env))}))

(def click
  (props
   {:as ::click
    :void :on-click
    :af click-af
    :ef click-ef}))
