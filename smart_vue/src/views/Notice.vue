<template>
  <div>
    <div style="margin: 10px 0">
      <el-input style="width: 200px" placeholder="请输入名称" suffix-icon="el-icon-search" v-model="name"></el-input>
      <el-button class="ml-5" type="primary" @click="load">搜索</el-button>
      <el-button type="warning" @click="reset">重置</el-button>
    </div>

    <div style="margin: 10px 0">
      <el-button type="primary" @click="handleAdd">新增 <i class="el-icon-circle-plus-outline"></i></el-button>
      <el-popconfirm
          class="ml-5"
          confirm-button-text='确定'
          cancel-button-text='我再想想'
          icon="el-icon-info"
          icon-color="red"
          title="您确定批量删除这些数据吗？"
          @confirm="delBatch"
      >
        <el-button type="danger" slot="reference">批量删除 <i class="el-icon-remove-outline"></i></el-button>
      </el-popconfirm>
      <el-button type="primary" @click="exp" class="ml-5">导出 <i class="el-icon-top"></i></el-button>
    </div>

    <el-table 
      :data="tableData" 
      border stripe 
      :header-cell-class-name="'headerBg'" 
      @selection-change="handleSelectionChange"
      style="width: 100%"
    >
      <el-table-column type="selection" width="55"></el-table-column>
      <el-table-column prop="id" label="ID" width="80" sortable></el-table-column>
      <el-table-column prop="name" label="标题" width="150"></el-table-column>
      <el-table-column prop="content" label="内容" min-width="200">
        <template slot-scope="scope">
          <div class="content-cell">{{ scope.row.content }}</div>
        </template>
      </el-table-column>
      <el-table-column prop="time" label="发布时间" width="180"></el-table-column>
      <el-table-column prop="user" label="发布人" width="120"></el-table-column>
      <!-- 图片列：支持多张图片预览 -->
      <el-table-column label="图片" width="160">
        <template slot-scope="scope">
          <div class="image-list">
            <el-image 
              v-for="(img, idx) in getImageList(scope.row.imgs)" 
              :key="idx"
              style="width: 50px; height: 50px; border-radius: 4px; margin-right: 4px; cursor: pointer;"
              :src="img"
              :preview-src-list="getImageList(scope.row.imgs)"
              fit="cover"
            >
            </el-image>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" align="center" fixed="right">
        <template slot-scope="scope">
          <el-button type="success" size="mini" @click="handleEdit(scope.row)">编辑</el-button>
          <el-popconfirm
              class="ml-5"
              confirm-button-text='确定'
              cancel-button-text='我再想想'
              icon="el-icon-info"
              icon-color="red"
              title="您确定删除吗？"
              @confirm="del(scope.row.id)"
          >
            <el-button type="danger" size="mini" slot="reference">删除</el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <div style="padding: 10px 0">
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="pageNum"
          :page-sizes="[2, 5, 10, 20]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
      </el-pagination>
    </div>

    <!-- 编辑弹窗 -->
    <el-dialog title="公告信息" :visible.sync="dialogFormVisible" width="50%" :close-on-click-modal="false">
      <el-form label-width="100px" size="small" style="width: 90%; margin: 0 auto">
        <el-form-item label="标题">
          <el-input v-model="form.name" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="内容">
          <el-input type="textarea" v-model="form.content" autocomplete="off" rows="6"></el-input>
        </el-form-item>
        <el-form-item label="发布时间">
          <el-date-picker v-model="form.time" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="选择日期时间"></el-date-picker>
        </el-form-item>
        <el-form-item label="发布人">
          <el-input v-model="form.user" autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="图片">
          <div class="upload-container">
            <el-upload
              action="http://localhost:8080/file/upload"
              ref="upload"
              :on-success="handleImgUploadSuccess"
              :on-remove="handleImgRemove"
              :file-list="imageFileList"
              list-type="picture-card"
              multiple
              :limit="6"
            >
              <i class="el-icon-plus"></i>
            </el-upload>
            <div style="font-size: 12px; color: #999; margin-top: 8px;">最多上传6张图片，点击图片可预览</div>
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="save">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "Notice",
  data() {
    return {
      tableData: [],
      total: 0,
      pageNum: 1,
      pageSize: 10,
      name: "",
      form: {
        imgs: ''  // 图片URL，用逗号分隔
      },
      imageFileList: [],  // 图片文件列表
      dialogFormVisible: false,
      multipleSelection: [],
      user: localStorage.getItem("user") ? JSON.parse(localStorage.getItem("user")) : {}
    }
  },
  created() {
    this.load()
  },
  methods: {
    // 获取图片列表（字符串转数组）
    getImageList(imgs) {
      if (!imgs) return []
      return imgs.split(',').filter(url => url.trim() !== '')
    },
    load() {
      this.request.get("/notice/page", {
        params: {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          name: this.name,
        }
      }).then(res => {
        this.tableData = res.data.records
        this.total = res.data.total
      })
    },
    save() {
      this.request.post("/notice", this.form).then(res => {
        if (res.code === '200') {
          this.$message.success("保存成功")
          this.dialogFormVisible = false
          this.load()
        } else {
          this.$message.error("保存失败")
        }
      })
    },
    handleAdd() {
      this.dialogFormVisible = true
      this.form = { imgs: '' }
      this.imageFileList = []
      this.$nextTick(() => {
        if (this.$refs.upload) {
          this.$refs.upload.clearFiles()
        }
      })
    },
    handleEdit(row) {
      this.form = JSON.parse(JSON.stringify(row))
      // 将图片字符串转为文件列表
      this.imageFileList = this.getImageList(row.imgs).map(url => ({
        url: url,
        name: url.split('/').pop()
      }))
      this.dialogFormVisible = true
    },
    // 图片上传成功
    handleImgUploadSuccess(response, file) {
      // response 是后端返回的图片URL
      const url = response
      // 添加到图片列表
      this.imageFileList.push({ url: url, name: file.name })
      // 更新 form.imgs
      this.updateImgUrls()
    },
    // 图片移除
    handleImgRemove(file, fileList) {
      this.imageFileList = fileList
      this.updateImgUrls()
    },
    // 更新图片URL字符串
    updateImgUrls() {
      const urls = this.imageFileList.map(item => item.url || item.response)
      this.form.imgs = urls.join(',')
    },
    del(id) {
      this.request.delete("/notice/" + id).then(res => {
        if (res.code === '200') {
          this.$message.success("删除成功")
          this.load()
        } else {
          this.$message.error("删除失败")
        }
      })
    },
    handleSelectionChange(val) {
      this.multipleSelection = val
    },
    delBatch() {
      if (!this.multipleSelection.length) {
        this.$message.error("请选择需要删除的数据")
        return
      }
      let ids = this.multipleSelection.map(v => v.id)
      this.request.post("/notice/del/batch", ids).then(res => {
        if (res.code === '200') {
          this.$message.success("批量删除成功")
          this.load()
        } else {
          this.$message.error("批量删除失败")
        }
      })
    },
    reset() {
      this.name = ""
      this.load()
    },
    handleSizeChange(pageSize) {
      this.pageSize = pageSize
      this.load()
    },
    handleCurrentChange(pageNum) {
      this.pageNum = pageNum
      this.load()
    },
    exp() {
      window.open("http://localhost:8080/notice/export")
    }
  }
}
</script>

<style>
.headerBg {
  background: #eee!important;
}

.content-cell {
  max-height: 80px;
  overflow-y: auto;
  line-height: 1.6;
  padding: 5px 0;
  word-wrap: break-word;
  white-space: normal;
}

.content-cell::-webkit-scrollbar {
  width: 4px;
}
.content-cell::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}
.content-cell::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}

.image-list .el-image {
  border: 1px solid #eee;
}

.upload-container {
  padding: 8px 0;
}

.el-upload--picture-card {
  width: 80px !important;
  height: 80px !important;
  line-height: 80px !important;
}
</style>