(ns comp.props
  (:require
   [ti-yong.alpha.transformer :as t]
   [ti-yong.alpha.util :as u]))

(def void
  (-> t/transformer
      (update :id conj ::void)
      (update :tf conj
              ::void
              (fn [{:as env :keys [props]
                    props-void :props/void}]
                (assoc env :props (apply dissoc props (u/muff props-void)))))))

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

(def props
  (-> void
      (update :id conj ::props)
      (update :tf-pre conj
              ::props-tf-pre
              (fn [{:as env :keys [props args]
                    ptfp :props/tf-pre}]
                (let [;; Run all props/tf-pre fns (each returns partial props to merge)
                      props (if-not (seq ptfp)
                              props
                              (reduce (fn [p [_id f]]
                                        (merge-with-styles p (f p env)))
                                      (or props {})
                                      (partition 2 ptfp)))
                      ;; Wrap function args with env binding
                      env (if-not (seq args)
                            (assoc env :props props)
                            (assoc env
                                   :props props
                                   :args (wrap-fns env args)))]
                  env)))
      (update :tf conj
              ::props-tf
              (fn [{:as env :keys [props args]
                    ptf :props/tf}]
                (let [;; Merge invocation-time map arg into props and env
                      prop-args? (map? (first args))
                      merged-props (merge-with-styles (or props {})
                                                      (when prop-args? (first args)))
                      env (if prop-args?
                            (-> env
                                (merge (first args))     ;; top-level env keys (for :on-click etc.)
                                (assoc :props merged-props)
                                (update :args #(vec (rest %))))
                            (assoc env :props merged-props))
                      ;; Run all props/tf fns (each returns partial props to merge)
                      final-props (if-not (seq ptf)
                                    (:props env)
                                    (reduce (fn [p [_id f]]
                                              (merge-with-styles p (f p env)))
                                            (:props env)
                                            (partition 2 ptf)))]
                  (assoc env :props final-props))))))
