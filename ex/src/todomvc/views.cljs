(ns todomvc.views
  (:require
   [clojure.string :as str]
   [comp.el :as comp]
   [todomvc.views.comps :as c]
   [todomvc.views.affects :as a]
   [todomvc.views.styled :as styled]
   [reagent.core :as reagent]
   [re-frame.core :refer [dispatch subscribe]]))

(defn todo-input
  [{:as env :keys [on-save on-stop]
    {:keys [title]} :todo}]
  (let [val  (reagent/atom title)
        stop #(do (reset! val "")
                  (when on-stop (on-stop)))
        save #(let [v (-> @val str str/trim)]
                (on-save v)
                (stop))]
    (fn [_props]
      (comp/raw-input
       {:is ::todo-input :with a/void-todo
        :props (merge styled/todo-input
                      env
                      {:auto-focus  true
                       :value       (or @val "")
                       :on-blur     save
                       :on-change   (fn [ev]
                                      (let [v (-> ev .-target .-value)]
                                        (reset! val v)))
                       :on-key-down #(case (.-which %)
                                       13 (save)
                                       27 (stop)
                                       nil)})}))))

(def new-todo
  (comp/el
   {:as ::new-todo
    :props (merge styled/new-todo
                  {:comp todo-input
                   :placeholder "What needs to be done?"})}))

(defn new-todo-box []
  (let [all-complete? @(subscribe [:all-complete?])]
    [comp/container styled/new-todo
     [comp/item {:xs 1
                 :on-click #(dispatch [:complete-all-toggle])}
      [comp/arrow-down {:font-size "large"
                        :style (merge {:padding-top 3
                                       :padding-left 0
                                       :color        "#e6e6e6"}
                                      (when all-complete?
                                        {:color "#737373"}))}]]
     [comp/item {:xs 11}
      [new-todo {:on-save #(when (seq %)
                             (dispatch [:add-todo %]))}]]]))

(defn todo-item []
  (let [editing (reagent/atom false)
        hover-state (reagent/atom false)]
    (fn [{{:as todo :keys [id]} :todo}]
      [comp/list-item
       (merge styled/todo-item
              {:on-mouse-over #(reset! hover-state true)
               :on-mouse-out #(reset! hover-state false)})
       [c/complete-check todo]
       (if-not @editing
         [c/todo-display (assoc todo :editing editing)]
         [new-todo
          (merge styled/edit-todo
                 {:todo todo
                  :placeholder ""
                  :on-save #(if (seq %)
                              (dispatch [:save id %])
                              (dispatch [:delete-todo id]))
                  :on-stop #(reset! editing false)})])
       (when @hover-state
         [c/delete-todo
          {:todo todo
           :is id}])])))

(def todo-list
  (comp/list-items
   {:as ::todo-list
    :children
    #(let [visible-todos @(subscribe [:visible-todos])]
       (->> visible-todos
            (mapv (fn [x] [todo-item {:todo x}]))
            (interpose [comp/divider])
            (into [[comp/divider]])))}))

(defn footer-selectors []
  (let [showing @(subscribe [:showing])]
    [comp/container
     [comp/item {:xs 3}
      [c/filter-all {:selected? showing}]]
     [comp/item {:xs 5}
      [c/filter-active {:selected? showing}]]
     [comp/item {:xs 4}
      [c/filter-done {:selected? showing}]]]))

(defn footer-controls []
  (let [[active done] @(subscribe [:footer-counts])]
    [comp/paper styled/footer-controls
     [comp/container
      [comp/item {:xs 2}
       active " " (case active 1 "item" "items") " left"]
      [comp/item {:xs 2}]
      [comp/item {:xs 3 :container true}
       [footer-selectors]]
      [comp/item {:xs 2}]
      [comp/item {:xs 3
                  :on-click #(dispatch [:clear-completed])
                  :style/hover {:text-decoration "underline"
                                :cursor "pointer"}}
       (when (pos? done)
         "Clear completed")]]]))

(def body-style
  {:padding "0 auto"
   :font "14px 'Helvetica Neue', Helvetica, Arial, sans-serif"
   :line-height "1.4em"
   :background "#f5f5f5"
   :color "#4d4d4d"
   :min-width "230px"
   :max-width "550px"
   :margin "0 auto"
  ;;  :margin 0
   :-webkit-font-smoothing "antialiased"
   :-moz-font-smoothing "antialiased"
   :font-smoothing "antialiased"
   :font-weight 300})

(def html-style
  {:padding 0
   :margin 0
   :background "#f5f5f5"})

(defn todo-app []
  ;[:html {:style html-style}]
  [:body {:style body-style}
   [comp/box {:style {:padding-top 50}}
    [c/todo-header-title "todos"]
    [comp/paper {:style {:padding-top 16
                         :box-shadow (str "0 2px 4px 0 rgba(0, 0, 0, 0.2),"
                                          "0 25px 50px 0 rgba(0, 0, 0, 0.1)")}}
     [new-todo-box]
     (when (seq @(subscribe [:todos]))
       [todo-list])
     [footer-controls]]
    [:footer styled/todo-app-footer
     [:p "Double-click to edit a todo"]]
    [comp/box {:style {:height 100}}]]])
