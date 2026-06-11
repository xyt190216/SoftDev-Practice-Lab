## 实验三：Jupyter Notebook 基础实践报告

- **课程名称**：软件项目研发实践
- **实验项目**：Jupyter Notebook 基础实践
- **学生姓名**：熊依婷
- **学号**：121052023050
- **专业班级**：软工2班
- **指导教师**：林立

## 一、 实验内容

本实验主要通过 Anaconda 安装并启动 Jupyter Notebook，熟悉其基本原理与交互式编程模式。结合历年财富世界 500 强的数据（1955-2005）进行综合实战，实验核心内容包括：

1. **Notebook 工具原理与操作**：掌握 Cell（单元格）的 Command（命令模式）与 Edit（编辑模式）切换，理解 Kernel（计算引擎）的跨单元格状态延续机制。
2. **Python 基本语法实践**：独立编写 `selection_sort` 选择排序算法，并定义 `test` 函数完成输入与测试。
3. **Pandas 数据预处理与清洗**：利用 Pandas DataFrame 加载 `fortune500.csv`，使用正则表达式定位 `profit`（利润）列中的非数字异常值（`"N.A."`），并对其进行过滤和格式转换。
4. **Matplotlib 综合数据可视化**：对清洗后的数据按年份（year）分组，完成一张“双 Y 轴”对比图，同时画出 500 强企业历年平均利润和平均收入的趋势。

## 二、 核心实现逻辑说明

完整可运行的代码已保存在 `HelloNotebook.ipynb` 中。

根据实验要求在一张图同时画利润和收入。通过 `ax.twinx()` 创建共享 X 轴的独立 Y 轴，解决了收入与利润量级相差巨大无法同尺度显示的问题。

```
# 提取年份分组均值
group_by_year = df.loc[:, ['year', 'revenue', 'profit']].groupby('year')
avgs = group_by_year.mean()
x = avgs.index

fig, ax1 = plt.subplots(figsize=(10, 5))
# 左 Y 轴绘制利润 (Profit)
ax1.plot(x, avgs.profit, color='tab:blue', label='Mean Profit')
ax1.set_ylabel('Profit (millions)', color='tab:blue')

# 右 Y 轴绘制收入 (Revenue)
ax2 = ax1.twinx()
ax2.plot(x, avgs.revenue, color='tab:orange', label='Mean Revenue')
ax2.set_ylabel('Revenue (millions)', color='tab:orange')

plt.title('Increase in mean Fortune 500 company profits and revenues')
plt.show()
```

## 三、 实验结果与截图佐证

以下为我本机运行 `HelloNotebook.ipynb` 时的关键过程截图及运行验证：

### 1. 截图 1：Jupyter Cell 单元格与 Kernel 运行验证

测试单元格执行机制。运行了教程中的 `print('Hello World!')` 输出以及 `time.sleep(3)`。观察到了左侧标签呈现 `In [*]` 的等待状态，以及跨 Cell 变量调用的执行效果。

> <img src="pictures\1.png" alt="1" style="zoom:70%;" />

### 2. 截图 2：选择排序算法执行验证

在代码 Cell 中定义了选择排序算法，并执行了测试数据。结果成功按照升序输出了数组，验证了 Python 基础代码编写的正确性。

> <img src="pictures\sort.png" alt="sort" style="zoom:70%;" />

### 3. 截图 3：Pandas 数据过滤清洗验证

输出 `df.dtypes` 观察到了 `profit` 列的异常。执行正则过滤 `df.loc[~non_numberic_profits]` 后，数据量由 25500 条下降至 25131 条，并且该列的类型成功由 `object` 变为了 `float64`。

> <img src="pictures\wash.png" alt="wash" style="zoom:70%;" />

### 4. 截图 4：多指标整合趋势图表展示

运行 Matplotlib 绘图代码，成功在一张图表内同时输出了蓝色曲线（利润）与橙色曲线（收入）。

> <img src="pictures\二合一.png" alt="二合一" style="zoom:75%;" />

## 四、 实验总结

通过本次数据分析入门实践，我不仅掌握了 Jupyter Notebook 的快捷操作，还对 Python 数据科学生态有了初步认知：

1. **Kernel 的便利性**：在 Notebook 中，上面 Cell 导入的 `pandas`、`numpy` 或者预处理好的 `DataFrame` 对象，在下面的 Cell 中可以直接调用。这让数据探索与图表微调变得极其高效，不必每次都重新运行耗时的数据加载。
2. **数据清洗是分析的核心前提**：教程中指出包含非数字记录（N.A.）的比例不足 4%。利用正则表达式 `[^0-9.-]` 找出并排除这些脏数据，是实现后续 `.mean()` 求均值计算的关键步骤。
3. **可视化直观揭示规律**：在最终整合的双指标趋势图中可以直观地看见50年以来的经济利润趋势，这充分体现了 Matplotlib 配合数据分析的强大威力。