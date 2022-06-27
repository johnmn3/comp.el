(ns todomvc.views
  (:require
   [perc.core]
   [injest.path :refer [+> +>> x>>]]
   [comp.el :as comp]
   [todomvc.views.comps :as c]
   [todomvc.views.affects :as a]
   [todomvc.views.styled :as styled]
   [reagent.core :as reagent]
   [re-frame.core :refer [dispatch subscribe]]))

(def new-todo-box
  (comp/container
   {:as ::new-todo-box :with styled/new-todo}
   #%[[c/toggle-all-down-arrow]
      [comp/item {:xs 11}
       [c/new-todo]]]))

(def todo-item
  (comp/list-item
   {:as ::todo-item :with a/void-todo
    :props styled/todo-item
    :props/ef #%{:on-mouse-over #%%(reset! %:hover-state true)
                 :on-mouse-out #%%(reset! %:hover-state false)}}
  #%[[c/complete-check %:props:todo]
     (if-not @%:props:editing
       [c/todo-display (assoc %:props:todo :editing %:props:editing)
        %:props:todo:title]
       [(c/existing-todo
         {:as ::edit-existing-todo
          :props {:todo %:props:todo
                  :editing %:props:editing}})])
     (when @%:props:hover-state
       [c/delete-todo {:is %:props:todo:id}])]))

(def todo-list
  (comp/list-items
   {:as ::todo-list}
   #%(let [visible-todos @(subscribe [:visible-todos])]
       (+>> visible-todos
            (mapv #%%[todo-item
                      {:is ::list-todo
                       :props {:todo %%
                               :editing (reagent/atom false)
                               :hover-state (reagent/atom false)}}])
            (interpose [comp/divider])
            (into [[comp/divider]])))))

(defn todo-app []
  [:div {:style styled/body-style}
   [comp/box {:style {:padding-top 50}}
    [c/todo-header-title "todos"]
    [comp/paper {:style {:padding-top 16
                         :box-shadow (str "0 2px 4px 0 rgba(0, 0, 0, 0.2),"
                                          "0 25px 50px 0 rgba(0, 0, 0, 0.1)")}}
     [new-todo-box]
     (when (seq @(subscribe [:todos]))
       [todo-list])
     [c/footer-controls]]
    [:footer styled/todo-app-footer
     [:p "Double-click to edit a todo"]]
    [comp/box {:style {:height 100}}]]])
