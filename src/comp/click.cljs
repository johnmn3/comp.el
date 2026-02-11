(ns comp.click
  (:require
   [comp.props :as p :refer [props props-tf-pre-fn props-tf-fn void-tf]]))

(defn click-af
  "Reads :on-click/:mk-on-click from env and stores the resolved handler
   in ::click-handler (in env, NOT in :props). This survives ::void stripping."
  [{:as env :keys [on-click mk-on-click]}]
  (when (or mk-on-click on-click)
    {::click-handler (cond mk-on-click (mk-on-click env)
                           on-click    #(on-click %)
                           :else       identity)}))

(defn click-ef
  "Reads ::click-handler from env (set by click-af, untouched by void)
   and wraps it with env closure, placing the final DOM handler in :props."
  [{:as env :keys [::click-handler]}]
  (when click-handler
    {:props (assoc (:props env) :on-click #(click-handler env))}))

(defn click-af-tf
  "Wrapper for click-af that merges result into env."
  [env]
  (if-let [result (click-af env)]
    (merge env result)
    env))

(defn click-ef-tf
  "Wrapper for click-ef that merges result into env."
  [env]
  (if-let [result (click-ef env)]
    (merge env result)
    env))

(def click
  (-> props
      (update :id conj ::click)
      (update :props/void #(into (or % []) [:on-click]))
      ;; Explicit :tf pipeline ordering:
      ;; ::props-tf-pre → ::props-tf → ::click-af → ::void → ::click-ef
      ;; - click-af reads :on-click from env top-level, stores in ::click-handler
      ;; - void strips internal keys from :props (including :on-click)
      ;; - click-ef reads ::click-handler from env, puts DOM handler into :props
      ;; IMPORTANT: Use ::p/ prefix for props/void IDs so they match
      ;; the keywords from comp.props during combine dedup.
      (assoc :tf [::p/props-tf-pre props-tf-pre-fn
                  ::p/props-tf     props-tf-fn
                  ::click-af       click-af-tf
                  ::p/void         void-tf
                  ::click-ef       click-ef-tf])))
