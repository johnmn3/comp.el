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

(defn props-joiner
  [{parent-props :props
    parent-props-void :props/void
    parent-props-af :props/af
    parent-props-ef :props/ef}
   {child-props :props
    child-props-void :props/void
    child-props-af :props/af
    child-props-ef :props/ef}]
  (merge {:props (merge-with-styles parent-props child-props)}
         (when (and parent-props-void child-props-void)
           {:props/void (vec (set (concat parent-props-void (u/muff child-props-void))))})
         (when (and parent-props-af child-props-af)
           {:props/af (comp child-props-af parent-props-af)})
         (when (and parent-props-ef child-props-ef)
           {:props/ef (comp child-props-ef parent-props-ef)})))

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
              ::props-af
              (fn [{:as env
                    :keys [props children args]
                    props-af :props/af}]
                (merge env
                       (when (seq args)
                         {:children (vec (concat (or children []) (wrap-fns env args)))
                          :args []})
                       (when props-af
                         {:props (merge-with-styles
                                  props
                                  (props-af props env))}))))
      (update :tf conj
              ::props-ef
              (fn [{:as env
                    :keys [props args]
                    props-ef :props/ef}]
                (let [prop-args? (-> args first map?)
                      merged-props (merge-with-styles props (when prop-args?
                                                              (first args)))]
                  (if-not props-ef
                    (assoc env :props merged-props)
                    (merge env
                           {:props (merge-with-styles merged-props (props-ef merged-props env))}
                           (when prop-args?
                             {:args (rest args)}))))))))
