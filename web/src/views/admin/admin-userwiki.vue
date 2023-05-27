<template>
  <a-layout>
    <a-layout-content
        :style="{ background: '#fff', padding: '24px', margin: 0, minHeight: '280px' }"
    >
      <p>
        <a-form
            layout="inline"
            :model="param">
          <a-form-item>
            <a-input v-model:value="param.loginName" placeholder="登录名">
            </a-input>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="handleQuery({page:1,size:pagination.pageSize})">
              查询
            </a-button>
          </a-form-item>
          <a-form-item>
            <a-button type="primary" @click="add()" >
              新增
            </a-button>
          </a-form-item>
        </a-form>
      </p>
      <!--      change:点击分页会调用这个方法-->
      <a-table
          :columns="columns"
          :row-key="record=>record.id"
          :data-source="userwikis"
          :pagination="pagination"
          :loading="loading"
          @change="handleTableChange"
      >

        <template v-slot:action="{text,record}">
          <a-space size="small">
            <!--           加上：会认为引号里面的都是变量-->

            <a-button type="primary" @click="resetPassword(record)">
              重置密码
            </a-button>
            <a-button type="primary" @click="edit(record)">
              编辑
            </a-button>

            <a-popconfirm
                title="删除后不可恢复，确认删除?"
                ok-text="是"
                cancel-text="否"
                @confirm="handleDelete(record.id)"
            >
              <a-button type="danger" >
                删除
              </a-button>
            </a-popconfirm>

          </a-space>
        </template>
      </a-table>
    </a-layout-content>
  </a-layout>

  <a-modal
      title="用户表单"
      v-model:visible="modalVisible"
      :confirm-loading="modalLoading"
      @ok="handleModalOk"
  >
    <a-form :model="userwiki" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
      <a-form-item label="登录名">
        <!--  !!两个非操作可以绕过前端的类型校验-->
        <a-input v-model:value="userwiki.loginName" :disabled="!!userwiki.id"/>
      </a-form-item>
      <a-form-item label="昵称">
        <a-input v-model:value="userwiki.name" />
      </a-form-item>
      <a-form-item label="密码" v-show="!userwiki.id">
        <a-input v-model:value="userwiki.password" />
      </a-form-item>

    </a-form>
  </a-modal>

  <a-modal
      title="重置密码"
      v-model:visible="resetModalVisible"
      :confirm-loading="resetModalLoading"
      @ok="handleResetModalOk"
  >
    <a-form :model="userwiki" :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">

      <a-form-item label="新密码" >
        <a-input v-model:value="userwiki.password" />
      </a-form-item>

    </a-form>
  </a-modal>
</template>
<script lang="ts">
import {defineComponent,onMounted,ref} from "vue";
import axios from "axios";
import {message} from "ant-design-vue";
import {Tool} from "@/util/tool";

declare let hexMd5: any;
declare let KEY: any;
export default defineComponent({
  name:'AdminUser',
  setup(){
    const param=ref();
    param.value={};
    const userwikis=ref();
    const pagination=ref({
      current:1,//当前页
      pageSize:10,//每页的分页条数
      total:0//
    });
    const loading=ref(false);
    const columns=[
      {
        title: '登录名',
        dataIndex: 'loginName',
      },
      {
        title: '名称',
        dataIndex: "name"
      },
      {
        title: '密码',
        dataIndex: "password"
      },
      {
        title: 'Action',
        key: 'action',
        slots: { customRender: 'action' }
      }
    ];
    /*
    * 数据查询
    * */
    const handleQuery=(params:any)=>{
      loading.value=true;
      userwikis.value = [];
      axios.get("/user_wiki/list",{
        params: {
          page:params.page,
          size:params.size,
          loginName:param.value.loginName  //从响应式变量中取值
        }
      }).then((respnse)=>{
        loading.value=false;
        const data=respnse.data;
        if (data.success){
          userwikis.value=data.content.list;
          //重置分页按钮
          pagination.value.current=params.page;
          pagination.value.total=data.content.total;
        }
        else {
          message.error(data.message)
        }
      });
    };
    /*
    * 表格点击页码时触发
    * */
    const handleTableChange=(pagination:any)=>{
      console.log("看看自带的分页参数都有啥："+pagination);
      handleQuery({
        page:pagination.current,
        size:pagination.pageSize
      });
    };
    //*表单*
    // 数组[100,101]对应：前端开发/Vue
    // /
    const userwiki=ref();
    const modalVisible = ref<boolean>(false);
    const modalLoading = ref<boolean>(false);
    const handleModalOk = () => {
      modalLoading.value = true;
      userwiki.value.password=hexMd5(userwiki.value.password+KEY);
      axios.post("/user_wiki/save",userwiki.value).then((respnse)=>{
        modalLoading.value = false;
        const data=respnse.data;  //data=commonResp
        if (data.success) {
          modalVisible.value = false;
          //重新加载列表
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize
          });
        }
        else{
          message.error(data.message)
        }

      });
    };
    //*
    // 编辑
    // */
    const edit=(record:any)=>{
      modalVisible.value=true;
      userwiki.value=Tool.copy(record);
    };
    //*
    // 新增
    // */
    const add=()=>{

      modalVisible.value=true;
      userwiki.value= {};
    };

    //*
    // 删除
    // */
    const handleDelete =(id: number)=>{
      axios.delete("/user_wiki/delete/"+id).then((respnse)=>{

        const data=respnse.data;  //data=commonResp
        if (data.success){

          //重新加载列表
          handleQuery({
            page: pagination.value.current,
            size:pagination.value.pageSize
          });
        }
      });
    };

    //*重置密码*
    //
    // /
    const resetModalVisible = ref<boolean>(false);
    const resetModalLoading = ref<boolean>(false);
    const handleResetModalOk = () => {
      resetModalLoading.value = true;
      userwiki.value.password=hexMd5(userwiki.value.password+KEY);
      axios.post("/user_wiki/reset-passwprd",userwiki.value).then((respnse)=>{
        resetModalLoading.value = false;
        const data=respnse.data;  //data=commonResp
        if (data.success) {
          resetModalVisible.value = false;
          //重新加载列表
          handleQuery({
            page: pagination.value.current,
            size: pagination.value.pageSize
          });
        }
        else{
          message.error(data.message)
        }
      });
    };
    //*
    // 重置密码
    // */
    const resetPassword=(record:any)=>{
      resetModalVisible.value=true;
      userwiki.value=Tool.copy(record);
      userwiki.value.password='';
    };

    onMounted(()=>{
      handleQuery({
        page: 1,
        size: pagination.value.pageSize,
      });
    });

    //将这些变量返回给html
    return{
      param,
      userwikis,
      pagination,
      columns,
      loading,
      handleTableChange,
      handleQuery,
      edit,
      add,

      userwiki,
      modalVisible,
      modalLoading,
      handleModalOk,


      resetModalVisible,
      resetModalLoading,
      handleResetModalOk,
      resetPassword,

      handleDelete,
    }
  }
});
</script>
<style scoped>
img {
  width: 50px;
  height: 50px;
}
</style>