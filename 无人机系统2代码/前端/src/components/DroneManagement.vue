<template>
  <div class="drone-management">
    <div v-if="notification.show" :class="'alert alert-' + notification.type + ' alert-dismissible fade show'" role="alert">
      {{ notification.text }}
      <button type="button" class="close" @click="notification.show = false">&times;</button>
    </div>

    <div class="row mb-3">
      <div class="col-md-6">
        <div class="input-group">
          <input
            v-model="searchKeyword"
            type="text"
            class="form-control"
            placeholder="请输入无人机名称或型号"
            @keyup.enter="handleSearch"
          />
          <div class="input-group-append">
            <button class="btn btn-primary" type="button" @click="handleSearch">搜索</button>
          </div>
        </div>
      </div>
      <div class="col-md-6 text-right">
        <button class="btn btn-success" @click="handleAdd">+ 新增无人机</button>
      </div>
    </div>

    <div class="table-responsive">
      <table class="table table-bordered table-striped">
        <thead class="thead-light">
          <tr>
            <th>ID</th>
            <th>无人机名称</th>
            <th>型号</th>
            <th>厂商</th>
            <th>重量(kg)</th>
            <th>最大高度(m)</th>
            <th>最大速度(km/h)</th>
            <th>电池容量(mAh)</th>
            <th>续航时间(分钟)</th>
            <th>摄像头分辨率</th>
            <th>状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-if="loading">
            <td colspan="13" class="text-center py-4">
              <div class="spinner-border text-primary" role="status">
                <span class="sr-only">加载中...</span>
              </div>
            </td>
          </tr>
          <tr v-else-if="droneList.length === 0">
            <td colspan="13" class="text-center py-4 text-muted">暂无数据</td>
          </tr>
          <tr v-for="drone in droneList" :key="drone.id">
            <td>{{ drone.id }}</td>
            <td>{{ drone.name }}</td>
            <td>{{ drone.model }}</td>
            <td>{{ drone.manufacturer }}</td>
            <td>{{ drone.weight }}</td>
            <td>{{ drone.maxAltitude }}</td>
            <td>{{ drone.maxSpeed }}</td>
            <td>{{ drone.batteryCapacity }}</td>
            <td>{{ drone.flightTime }}</td>
            <td>{{ drone.cameraResolution }}</td>
            <td>
              <span :class="drone.status === 1 ? 'badge badge-success' : 'badge badge-warning'">
                {{ drone.status === 1 ? '启用' : '禁用' }}
              </span>
            </td>
            <td>{{ drone.createTime }}</td>
            <td>
              <button class="btn btn-sm btn-primary mr-1" @click="handleEdit(drone)">编辑</button>
              <button class="btn btn-sm btn-danger" @click="handleDelete(drone)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <nav v-if="pagination.total > 0" class="d-flex justify-content-between align-items-center">
      <small class="text-muted">
        共 {{ pagination.total }} 条，每页
        <select v-model.number="pagination.pageSize" @change="handleSizeChange" class="custom-select custom-select-sm" style="width: auto; display: inline-block;">
          <option :value="5">5</option>
          <option :value="10">10</option>
          <option :value="20">20</option>
          <option :value="50">50</option>
        </select>
        条
      </small>
      <ul class="pagination mb-0">
        <li class="page-item" :class="{ disabled: pagination.pageNum <= 1 }">
          <a class="page-link" href="#" @click.prevent="handleCurrentChange(pagination.pageNum - 1)">&laquo;</a>
        </li>
        <li
          v-for="page in pageNumbers"
          :key="page"
          class="page-item"
          :class="{ active: page === pagination.pageNum }"
        >
          <a class="page-link" href="#" @click.prevent="handleCurrentChange(page)">{{ page }}</a>
        </li>
        <li class="page-item" :class="{ disabled: pagination.pageNum >= totalPages }">
          <a class="page-link" href="#" @click.prevent="handleCurrentChange(pagination.pageNum + 1)">&raquo;</a>
        </li>
      </ul>
    </nav>

    <div v-show="showAddDialog" class="modal-backdrop-custom" @click="showAddDialog = false">
      <div class="modal-dialog-custom" @click.stop>
        <div class="modal-content-custom">
          <div class="modal-header-custom">
            <h5 class="modal-title">{{ isEdit ? '编辑无人机' : '新增无人机' }}</h5>
            <button type="button" class="close" @click="showAddDialog = false">&times;</button>
          </div>
          <form @submit.prevent="handleSubmit">
            <div class="modal-body-custom">
              <div class="form-group">
                <label>无人机名称 <span class="text-danger">*</span></label>
                <input v-model="formData.name" type="text" class="form-control" placeholder="请输入无人机名称" required />
              </div>
              <div class="form-group">
                <label>型号 <span class="text-danger">*</span></label>
                <input v-model="formData.model" type="text" class="form-control" placeholder="请输入型号" required />
              </div>
              <div class="form-group">
                <label>厂商</label>
                <input v-model="formData.manufacturer" type="text" class="form-control" placeholder="请输入厂商" />
              </div>
              <div class="form-row">
                <div class="col">
                  <div class="form-group">
                    <label>重量(kg)</label>
                    <input v-model.number="formData.weight" type="number" step="0.1" class="form-control" placeholder="请输入重量" />
                  </div>
                </div>
                <div class="col">
                  <div class="form-group">
                    <label>最大高度(m)</label>
                    <input v-model.number="formData.maxAltitude" type="number" class="form-control" placeholder="请输入最大高度" />
                  </div>
                </div>
              </div>
              <div class="form-row">
                <div class="col">
                  <div class="form-group">
                    <label>最大速度(km/h)</label>
                    <input v-model.number="formData.maxSpeed" type="number" class="form-control" placeholder="请输入最大速度" />
                  </div>
                </div>
                <div class="col">
                  <div class="form-group">
                    <label>电池容量(mAh)</label>
                    <input v-model.number="formData.batteryCapacity" type="number" class="form-control" placeholder="请输入电池容量" />
                  </div>
                </div>
              </div>
              <div class="form-row">
                <div class="col">
                  <div class="form-group">
                    <label>续航时间(分钟)</label>
                    <input v-model.number="formData.flightTime" type="number" class="form-control" placeholder="请输入续航时间" />
                  </div>
                </div>
                <div class="col">
                  <div class="form-group">
                    <label>摄像头分辨率</label>
                    <select v-model="formData.cameraResolution" class="form-control">
                      <option value="">请选择分辨率</option>
                      <option value="1080P">1080P</option>
                      <option value="2K">2K</option>
                      <option value="4K">4K</option>
                      <option value="其他">其他</option>
                    </select>
                  </div>
                </div>
              </div>
              <div class="form-group">
                <label>状态</label>
                <div class="form-check form-check-inline">
                  <input class="form-check-input" type="radio" name="status" id="statusActive" :value="1" v-model.number="formData.status" />
                  <label class="form-check-label" for="statusActive">启用</label>
                </div>
                <div class="form-check form-check-inline">
                  <input class="form-check-input" type="radio" name="status" id="statusInactive" :value="0" v-model.number="formData.status" />
                  <label class="form-check-label" for="statusInactive">禁用</label>
                </div>
              </div>
            </div>
            <div class="modal-footer-custom">
              <button type="button" class="btn btn-secondary" @click="showAddDialog = false">取消</button>
              <button type="submit" class="btn btn-primary" :disabled="submitLoading">
                <span v-if="submitLoading" class="spinner-border spinner-border-sm mr-2" role="status"></span>
                确定
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { getDrones, addDrone, updateDrone, deleteDrone } from '../api/drone'

// 搜索关键词
const searchKeyword = ref('')
// 加载状态
const loading = ref(false)
// 新增/编辑弹窗显示控制
const showAddDialog = ref(false)
// 是否为编辑模式
const isEdit = ref(false)
// 提交按钮加载状态
const submitLoading = ref(false)

// 无人机列表数据
const droneList = ref([])

// 分页信息
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

// 表单数据（新增/编辑共用）
const formData = reactive({
  id: null,
  name: '',
  model: '',
  manufacturer: '',
  weight: null,
  maxAltitude: null,
  maxSpeed: null,
  batteryCapacity: null,
  flightTime: null,
  cameraResolution: '',
  status: 1
})

// 操作提示通知
const notification = reactive({
  show: false,
  text: '',
  type: 'success'
})

// 计算总页数
const totalPages = computed(() => Math.ceil(pagination.total / pagination.pageSize) || 1)

// 计算分页按钮列表（最多显示5个页码）
const pageNumbers = computed(() => {
  const current = pagination.pageNum
  const total = totalPages.value
  const pages = []
  let start = Math.max(1, current - 2)
  let end = Math.min(total, current + 2)
  if (end - start < 4) {
    if (start === 1) end = Math.min(total, start + 4)
    else start = Math.max(1, end - 4)
  }
  for (let i = start; i <= end; i++) pages.push(i)
  return pages
})

// 显示操作提示通知（3秒后自动消失）
const notify = (text, type = 'success') => {
  notification.show = true
  notification.text = text
  notification.type = type
  setTimeout(() => { notification.show = false }, 3000)
}

// 重置表单数据到初始状态
const initFormData = () => {
  formData.id = null
  formData.name = ''
  formData.model = ''
  formData.manufacturer = ''
  formData.weight = null
  formData.maxAltitude = null
  formData.maxSpeed = null
  formData.batteryCapacity = null
  formData.flightTime = null
  formData.cameraResolution = ''
  formData.status = 1
}

// 获取无人机列表（携带分页和搜索条件）
const fetchDrones = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      keyword: searchKeyword.value
    }
    const response = await getDrones(params)
    if (response.code === 200) {
      droneList.value = response.data.list
      pagination.total = response.data.total
      pagination.pageNum = response.data.pageNum
      pagination.pageSize = response.data.pageSize
    }
  } catch (error) {
    notify('获取无人机列表失败', 'danger')
  } finally {
    loading.value = false
  }
}

// 搜索按钮点击：重置到第一页并重新查询
const handleSearch = () => {
  pagination.pageNum = 1
  fetchDrones()
}

// 每页数量变更：重置到第一页并重新查询
const handleSizeChange = () => {
  pagination.pageNum = 1
  fetchDrones()
}

// 页码切换：跳转到指定页
const handleCurrentChange = (val) => {
  if (val < 1 || val > totalPages.value) return
  pagination.pageNum = val
  fetchDrones()
}

// 新增按钮点击：打开空表单弹窗
const handleAdd = () => {
  isEdit.value = false
  initFormData()
  showAddDialog.value = true
}

// 编辑按钮点击：填充当前行数据到表单并打开弹窗
const handleEdit = (row) => {
  isEdit.value = true
  initFormData()
  formData.id = row.id
  formData.name = row.name
  formData.model = row.model
  formData.manufacturer = row.manufacturer
  formData.weight = row.weight
  formData.maxAltitude = row.maxAltitude
  formData.maxSpeed = row.maxSpeed
  formData.batteryCapacity = row.batteryCapacity
  formData.flightTime = row.flightTime
  formData.cameraResolution = row.cameraResolution
  formData.status = row.status
  showAddDialog.value = true
}

// 删除按钮点击：确认后调用删除接口
const handleDelete = (row) => {
  if (!window.confirm(`确定要删除无人机「${row.name}」吗？`)) return
  deleteDrone(row.id).then(() => {
    notify('删除成功')
    fetchDrones()
  }).catch(() => {
    notify('删除失败', 'danger')
  })
}

// 表单提交：校验必填字段后调用新增或更新接口
const handleSubmit = async () => {
  if (!formData.name || !formData.model) {
    notify('请填写必填字段：名称和型号', 'warning')
    return
  }
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateDrone(formData.id, formData)
      notify('更新成功')
    } else {
      await addDrone(formData)
      notify('添加成功')
    }
    showAddDialog.value = false
    fetchDrones()
  } catch (error) {
    notify(isEdit.value ? '更新失败' : '添加失败', 'danger')
  } finally {
    submitLoading.value = false
  }
}

// 组件挂载后自动加载无人机列表
onMounted(() => {
  fetchDrones()
})
</script>

<style scoped>
.drone-management {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.text-right {
  text-align: right;
}

.mr-1 {
  margin-right: 0.25rem;
}

.modal-backdrop-custom {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1050;
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal-dialog-custom {
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-content-custom {
  background: white;
  border-radius: 8px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.modal-header-custom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #dee2e6;
}

.modal-body-custom {
  padding: 20px;
}

.modal-footer-custom {
  display: flex;
  justify-content: flex-end;
  padding: 15px 20px;
  border-top: 1px solid #dee2e6;
  gap: 8px;
}

.modal-title {
  margin: 0;
  font-size: 1.25rem;
  font-weight: 500;
}
</style>