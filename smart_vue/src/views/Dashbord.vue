<template>
  <div>
    <div class="select">
      <el-select v-model="selectedOption">
        <el-option v-for="option in options" :key="option.value" :value="option.value" :label="option.label"></el-option>
      </el-select>
    </div>

    <!-- Flex 容器 - 两行两列 -->
    <div class="charts-wrapper">
      <!-- 第一行：环境数据 + 设备统计 -->
      <div class="row">
        <div class="chart-item" ref="chart1" style="height: 400px;"></div>
        <div class="chart-item" ref="chart2" style="height: 400px;"></div>
      </div>
      <!-- 第二行：实时数据 + 视频监控 -->
      <div class="row">
        <div class="chart-item" ref="chart3" style="height: 400px;"></div>
        <div class="video-container">
          <div class="type">视频监控</div>
          <video ref="videoPlayer" muted autoplay loop controls style="width: 100%; height: 350px; object-fit: cover;">
            <source :src="videoUrls[selectedOption - 1]" type="video/mp4">
          </video>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
const echarts = require('echarts');

export default {
  data() {
    return {
      selectedOption: '',
      options: [
        { label: 'A1号田', value: '1' },
        { label: 'A2号田', value: '2' },
        { label: 'A3号田', value: '3' }
      ],
      videoUrls: [
        require('@/assets/video/d01.mp4'),
        require('@/assets/video/d02.mp4'),
        require('@/assets/video/d01.mp4'),
        require('@/assets/video/d02.mp4')
      ],
      charts: [
        {
          id: 'chart1',
          1: {
            title: { text: '环境数据' },
            tooltip: {
              trigger: 'axis',
              axisPointer: { type: 'cross', label: { backgroundColor: '#6a7985' } }
            },
            legend: {
              right: 0,
              top: 25,
              textStyle: { fontSize: 10 },
              data: ['温度（℃）', '空气湿度（%）', '土壤湿度（%）', '二氧化碳含量（ppm）', '土壤PH', '光照强度(Lux)']
            },
            toolbox: { top: 2, feature: { saveAsImage: {} } },
            grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
            xAxis: [{ type: 'category', boundaryGap: false, data: ['星期一', '星期二', '星期三', '星期四', '星期五', '星期六', '星期日'] }],
            yAxis: [{ type: 'value' }],
            series: [
              { name: '温度（℃）', type: 'line', stack: 'Total', areaStyle: {}, emphasis: { focus: 'series' }, data: [20, 32, 10, 14, 19, 23, 21] },
              { name: '空气湿度（%）', type: 'line', stack: 'Total', areaStyle: {}, emphasis: { focus: 'series' }, data: [60, 72, 61, 54, 57, 52, 70] },
              { name: '土壤湿度（%）', type: 'line', stack: 'Total', areaStyle: {}, emphasis: { focus: 'series' }, data: [50, 32, 20, 44, 40, 30, 35] },
              { name: '二氧化碳含量（ppm）', type: 'line', stack: 'Total', areaStyle: {}, emphasis: { focus: 'series' }, data: [320, 432, 501, 434, 390, 430, 620] },
              { name: '土壤PH', type: 'line', stack: 'Total', areaStyle: {}, emphasis: { focus: 'series' }, data: [6.2, 7.0, 6.8, 8.4, 6.3, 8.0, 6.0] },
              { name: '光照强度(Lux)', type: 'line', stack: 'Total', label: { show: true, position: 'top' }, areaStyle: {}, emphasis: { focus: 'series' }, data: [2000, 1932, 1901, 1934, 2090, 2330, 2620] }
            ]
          },
          2: {
            title: { text: '环境数据' },
            tooltip: {
              trigger: 'axis',
              axisPointer: { type: 'cross', label: { backgroundColor: '#6a7985' } }
            },
            legend: {
              right: 0,
              top: 25,
              textStyle: { fontSize: 10 },
              data: ['温度（℃）', '空气湿度（%）', '土壤湿度（%）', '二氧化碳含量（ppm）', '土壤PH', '光照强度(Lux)']
            },
            toolbox: { top: 2, feature: { saveAsImage: {} } },
            grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
            xAxis: [{ type: 'category', boundaryGap: false, data: ['星期一', '星期二', '星期三', '星期四', '星期五', '星期六', '星期日'] }],
            yAxis: [{ type: 'value' }],
            series: [
              { name: '温度（℃）', type: 'line', stack: 'Total', areaStyle: {}, emphasis: { focus: 'series' }, data: [20, 32, 10, 34, 19, 23, 21] },
              { name: '空气湿度（%）', type: 'line', stack: 'Total', areaStyle: {}, emphasis: { focus: 'series' }, data: [62, 82, 91, 4, 90, 59, 73] },
              { name: '土壤湿度（%）', type: 'line', stack: 'Total', areaStyle: {}, emphasis: { focus: 'series' }, data: [50, 32, 20, 54, 40, 33, 41] },
              { name: '二氧化碳含量（ppm）', type: 'line', stack: 'Total', areaStyle: {}, emphasis: { focus: 'series' }, data: [620, 532, 701, 634, 690, 530, 620] },
              { name: '土壤PH', type: 'line', stack: 'Total', areaStyle: {}, emphasis: { focus: 'series' }, data: [8.0, 9.2, 9.1, 9.4, 7.0, 6.3, 7.6] },
              { name: '光照强度(Lux)', type: 'line', stack: 'Total', label: { show: true, position: 'top' }, areaStyle: {}, emphasis: { focus: 'series' }, data: [2820, 1932, 2901, 1934, 2290, 3330, 2320] }
            ]
          },
          3: {
            title: { text: '环境数据' },
            tooltip: {
              trigger: 'axis',
              axisPointer: { type: 'cross', label: { backgroundColor: '#6a7985' } }
            },
            legend: {
              right: 0,
              top: 25,
              textStyle: { fontSize: 10 },
              data: ['温度（℃）', '空气湿度（%）', '土壤湿度（%）', '二氧化碳含量（ppm）', '土壤PH', '光照强度(Lux)']
            },
            toolbox: { top: 2, feature: { saveAsImage: {} } },
            grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
            xAxis: [{ type: 'category', boundaryGap: false, data: ['星期一', '星期二', '星期三', '星期四', '星期五', '星期六', '星期日'] }],
            yAxis: [{ type: 'value' }],
            series: [
              { name: '温度（℃）', type: 'line', stack: 'Total', areaStyle: {}, emphasis: { focus: 'series' }, data: [30, 22, 21, 34, 27, 20, 33] },
              { name: '空气湿度（%）', type: 'line', stack: 'Total', areaStyle: {}, emphasis: { focus: 'series' }, data: [60, 82, 91, 64, 90, 30, 70] },
              { name: '土壤湿度（%）', type: 'line', stack: 'Total', areaStyle: {}, emphasis: { focus: 'series' }, data: [50, 32, 20, 54, 60, 30, 41] },
              { name: '二氧化碳含量（ppm）', type: 'line', stack: 'Total', areaStyle: {}, emphasis: { focus: 'series' }, data: [720, 832, 601, 734, 590, 730, 820] },
              { name: '土壤PH', type: 'line', stack: 'Total', areaStyle: {}, emphasis: { focus: 'series' }, data: [8.0, 9.2, 7.1, 9.4, 10.0, 7.4, 6.8] },
              { name: '光照强度(Lux)', type: 'line', stack: 'Total', label: { show: true, position: 'top' }, areaStyle: {}, emphasis: { focus: 'series' }, data: [2820, 2932, 1901, 3934, 2290, 3330, 2320] }
            ]
          }
        },
        {
          id: 'chart2',
          1: {
            title: { text: '设备统计', left: 0 },
            tooltip: { trigger: 'item' },
            legend: { right: 10, orient: 'vertical' },
            series: [{
              name: '设备(台)',
              type: 'pie',
              radius: '50%',
              data: [
                { value: 20, name: '采集设备' },
                { value: 10, name: '控制设备' },
                { value: 4, name: '气象站' },
                { value: 2, name: '集控一体' },
                { value: 4, name: '监控设备' }
              ],
              emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' } }
            }]
          },
          2: {
            title: { text: '设备统计', left: 0 },
            tooltip: { trigger: 'item' },
            legend: { right: 10, orient: 'vertical' },
            series: [{
              name: '设备(台)',
              type: 'pie',
              radius: '50%',
              data: [
                { value: 20, name: '采集设备' },
                { value: 10, name: '控制设备' },
                { value: 4, name: '气象站' },
                { value: 2, name: '集控一体' },
                { value: 4, name: '监控设备' }
              ],
              emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' } }
            }]
          },
          3: {
            title: { text: '设备统计', left: 0 },
            tooltip: { trigger: 'item' },
            legend: { right: 10, orient: 'vertical' },
            series: [{
              name: '设备(台)',
              type: 'pie',
              radius: '50%',
              data: [
                { value: 30, name: '采集设备' },
                { value: 20, name: '控制设备' },
                { value: 4, name: '气象站' },
                { value: 4, name: '集控一体' },
                { value: 4, name: '监控设备' }
              ],
              emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0, 0, 0, 0.5)' } }
            }]
          }
        },
        {
          id: 'chart3',
          1: {
            title: { text: '实时数据' },
            legend: { data: ['当前数值', '正常数值'] },
            tooltip: { trigger: 'item' },
            radar: {
              indicator: [
                { name: '温度（℃）', max: 60 },
                { name: '空气湿度（%）', max: 100 },
                { name: '土壤湿度（%）', max: 100 },
                { name: '二氧化碳含量（ppm）', max: 3000 },
                { name: '土壤PH', max: 14 },
                { name: '光照强度（Lux）', max: 4000 }
              ]
            },
            series: [{
              name: '实时数据',
              type: 'radar',
              data: [
                { value: [31, 70, 40, 950, 8.2, 1800], name: '当前数值' },
                { value: [26, 70, 50, 1500, 7.0, 2100], name: '正常数值' }
              ]
            }]
          },
          2: {
            title: { text: '实时数据' },
            legend: { data: ['当前数值', '正常数值'] },
            tooltip: { trigger: 'item' },
            radar: {
              indicator: [
                { name: '温度（℃）', max: 60 },
                { name: '空气湿度（%）', max: 100 },
                { name: '土壤湿度（%）', max: 100 },
                { name: '二氧化碳含量（ppm）', max: 3000 },
                { name: '土壤PH', max: 14 },
                { name: '光照强度（Lux）', max: 4000 }
              ]
            },
            series: [{
              name: '实时数据',
              type: 'radar',
              data: [
                { value: [20, 50, 30, 750, 6.0, 2000], name: '当前数值' },
                { value: [26, 70, 50, 1500, 7.0, 2100], name: '正常数值' }
              ]
            }]
          },
          3: {
            title: { text: '实时数据' },
            legend: { data: ['当前数值', '正常数值'] },
            tooltip: { trigger: 'item' },
            radar: {
              indicator: [
                { name: '温度（℃）', max: 60 },
                { name: '空气湿度（%）', max: 100 },
                { name: '土壤湿度（%）', max: 100 },
                { name: '二氧化碳含量（ppm）', max: 3000 },
                { name: '土壤PH', max: 14 },
                { name: '光照强度（Lux）', max: 4000 }
              ]
            },
            series: [{
              name: '实时数据',
              type: 'radar',
              data: [
                { value: [27, 80, 43, 1735, 7, 2200], name: '当前数值' },
                { value: [26, 70, 50, 1500, 7.0, 2100], name: '正常数值' }
              ]
            }]
          }
        }
      ]
    };
  },
  mounted() {
    const chartRefs = ['chart1', 'chart2', 'chart3'];
    chartRefs.forEach((refName, index) => {
      setTimeout(() => {
        const dom = this.$refs[refName];
        if (dom) {
          const chart = this.charts[index];
          chart.instance = echarts.init(dom);
          if (!this.selectedOption) {
            this.selectedOption = this.options[0].value;
          }
          chart.instance.setOption(chart[this.selectedOption]);
        }
      }, 100 + index * 50);
    });
  },
  watch: {
    selectedOption(newVal) {
      this.charts.forEach(chart => {
        if (chart.instance) {
          chart.instance.dispose();
          chart.instance = null;
        }
        const dom = this.$refs[chart.id];
        if (dom) {
          chart.instance = echarts.init(dom);
          chart.instance.setOption(chart[newVal]);
          chart.instance.resize();
        }
      });
      if (this.$refs.videoPlayer) {
        this.$refs.videoPlayer.load();
      }
    }
  },
  destroyed() {
    this.charts.forEach(chart => {
      if (chart.instance) {
        chart.instance.dispose();
        chart.instance = null;
      }
    });
  },
  methods: {
    play() {
      this.$refs.videoPlayer.play();
    },
    pause() {
      this.$refs.videoPlayer.pause();
    }
  }
};
</script>

<style scoped>
.select {
  background-color: white;
  color: black;
  border: none;
  font-size: 14px;
  padding: 8px;
  border-bottom: 1px solid #f3f0f0;
  margin-bottom: 10px;
}

.charts-wrapper {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.row {
  display: flex;
  gap: 10px;
}

.chart-item {
  flex: 1;
  background-color: rgb(248, 248, 248);
  padding: 10px;
  border-radius: 15px;
  box-sizing: border-box;
  min-width: 0;
}

.video-container {
  flex: 1;
  background-color: rgb(248, 248, 248);
  border-radius: 15px;
  padding: 10px;
  box-sizing: border-box;
  min-width: 0;
}

.type {
  font-family: Arial;
  font-size: 20px;
  color: #333;
  font-weight: bold;
  padding: 10px 0;
}

video {
  object-fit: cover;
  border-radius: 8px;
  width: 100%;
}

video:focus {
  outline: -webkit-focus-ring-color auto 0px;
}

/* 小屏幕适配 */
@media screen and (max-width: 768px) {
  .row {
    flex-direction: column;
  }
}
</style>