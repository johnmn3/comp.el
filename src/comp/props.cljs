(ns comp.props
  (:require
   [af.fect :as af]))

(def void
  (af/fect
   {:as ::void
    :ef-end (fn [{:keys [props]
                  props-void :props/void}]
              {:props (apply dissoc props (af/muff props-void))})}))

(defn merge-with-styles
  [{:as parent-props parent-style :style}
   {:as child-props child-style :style}]
  (merge parent-props
         child-props
         (when (and parent-style child-style)
           {:style (merge parent-style child-style)})))

(defn wrap-props [afn]
  (if-not afn
    (fn [env] env #_ {})
    (if (-> afn meta :wrapped?)
      afn
      (with-meta
        (fn
          ([props env]
           (merge env {:props (merge props (afn props env))}))
          ([env]
           (merge (:props env) (afn (:props env) env))))
        {:wrapped? true}))))

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
           {:props/void (vec (set (concat parent-props-void (af/muff child-props-void))))})
         (when (and parent-props-af child-props-af)
           {:props/af (comp (wrap-props child-props-af) (wrap-props parent-props-af))})
         (when (and parent-props-ef child-props-ef)
           {:props/ef (comp (wrap-props child-props-ef) (wrap-props parent-props-ef))})))

(defn wrap-fns [args]
  (->> args
       (mapv (fn [arg]
               (if (fn? arg)
                 (with-meta
                   (fn [& env]
                     (apply arg env))
                   {:ef/runnable? true})
                 arg)))))

(def props
  (void
   {:as ::props
    :join props-joiner
    :af (fn [{:as env
              :keys [props children args]
              props-af :props/af}]
          (merge {}
                 (when (seq args)
                   {:children (vec (concat (or children []) (wrap-fns args)))
                    :args []})
                 (when props-af
                   {:props (merge-with-styles
                            props
                            (:props
                             ((wrap-props props-af)
                              props env)))})))
    :ef (fn [{:as env
              :keys [props args]
              props-ef :props/ef
              :or {props-ef identity}}]
          (let [prop-args? (-> args first map?)
                merged-props (merge-with-styles props (when prop-args?
                                                        (first args)))
                merged-env (merge env
                                  {:props merged-props}
                                  (when prop-args?
                                    {:args (rest args)}))]
            (if-not props-ef
              (merge {:props merged-props}
                     (when prop-args?
                       {:args (rest args)}))
              (merge {:props (merge-with-styles
                              merged-props
                              (:props ((wrap-props props-ef)
                                       (:props merged-env) merged-env)))}
                     (when prop-args?
                       {:args (rest args)})))))}))
