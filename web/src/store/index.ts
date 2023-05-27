import { createStore } from 'vuex'
declare let SessionStorage:any;
//vuex :全局的响应式变量
const USER="USER"
const store=createStore({
  state: {
    // || 避免空指针异常，刷新页面 页面先加载index文件
    user:SessionStorage.get("USER")||{}
  },
  mutations: {
    setUser(state,user){
      state.user=user;
      SessionStorage.set(USER,user);
    }
  },
  actions: {
  },
  modules: {
  }
})
export default store;
