(ns comp.props
  (:require
   [ti-yong.alpha.transformer :as t]
   [ti-yong.alpha.util :as u]))

(defn void-tf
  "Strips :props/void keys from :props - should run after all prop transforms."
  [{:as env :keys [props]
    props-void :props/void}]
  (assoc env :props (apply dissoc props (u/muff props-void))))

;; void is a data-only base: initializes prop vectors but does NOT include
;; ::void in :tf.  This prevents user `:with` entries that extend void
;; (e.g. void-todo) from introducing an extra ::void that would mess up
;; the pipeline ordering via combine's last-occurrence-wins dedup.
(def void
  (-> t/transformer
      (update :id conj ::void)
      (assoc :props/void [] :props/tf-pre [] :props/tf [])))

(defn merge-with-styles
  [{:as parent-props parent-style :style}
   {:as child-props child-style :style}]
  (merge parent-props
         child-props
         (when (and parent-style child-style)
           {:style (merge parent-style child-style)})))

(defn wrap-fns [env args]
  (->> args
       (mapv (fn [arg]
               (if (fn? arg)
                 (with-meta
                   (fn [_env]
                     (arg env))
                   {:ef/runnable? true})
                 arg)))))

(defn props-tf-pre-fn
  "Runs :props/tf-pre transforms and wraps function args."
  [{:as env :keys [props args]
    ptfp :props/tf-pre}]
  (let [props (if-not (seq ptfp)
                props
                (reduce (fn [p [_id f]]
                          (merge-with-styles p (f p env)))
                        (or props {})
                        (partition 2 ptfp)))
        env (if-not (seq args)
              (assoc env :props props)
              (assoc env
                     :props props
                     :args (wrap-fns env args)))]
    env))

(defn props-tf-fn
  "Merges invocation-time map args into env and runs :props/tf transforms."
  [{:as env :keys [props args]
    ptf :props/tf}]
  (let [prop-args? (map? (first args))
        merged-props (merge-with-styles (or props {})
                                        (when prop-args? (first args)))
        env (if prop-args?
              (-> env
                  (merge (first args))
                  (assoc :props merged-props)
                  (update :args #(vec (rest %))))
              (assoc env :props merged-props))
        final-props (if-not (seq ptf)
                      (:props env)
                      (reduce (fn [p [_id f]]
                                (merge-with-styles p (f p env)))
                              (:props env)
                              (partition 2 ptf)))]
    (assoc env :props final-props)))

(def props
  (-> void
      (update :id conj ::props)
      ;; Rebuild :tf with correct order:
      ;; ::props-tf-pre → ::props-tf → ::void (void must strip AFTER all props are set)
      (assoc :tf [::props-tf-pre props-tf-pre-fn
                  ::props-tf     props-tf-fn
                  ::void         void-tf])))
