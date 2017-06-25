//分部分项
Ext.onReady(function() {
	Ext.tip.QuickTipManager.init();
	Ext.define('Budget.app.bussinessProcess.ProjectBitPanel', {
		extend : 'Ext.panel.Panel',
		width: "100%",  
	    defaults: {  
           collapsible: true, // 支持该区域的展开和折叠  
        },
		initComponent : function() {
			var me = this;
			Ext.apply(this, {
				layout : 'border',
				height:  500,
				items : [
					Ext.create("Budget.app.bussinessProcess.ProjectBitPanel.BitItemGrid", {
						bitProjectId:me.bitProjectId,
						id:"projectbitpanel-bititemgrid"
				}),Ext.create("Budget.app.bussinessProcess.ProjectBitPanel.BitDetailGrid", {
						bitProjectId:me.bitProjectId,
						id:"projectbitpanel-bitdetailgrid"
				})]
			});
			this.callParent(arguments);
		}
	});
	
	//子目页面
	Ext.define('Budget.app.bussinessProcess.ProjectBitPanel.BitItemGrid', {
		extend : 'Ext.tree.Panel',
		region : 'north',
		height : '50%',
		header: false,
		rootVisible: false,
		plugins:[{
  	        ptype: 'cellediting',
  	        clicksToEdit: 1,
  	        editing:false, 
  	        listeners:{
  	        	edit:function(editor , context , eOpts){
  	        	}
  	        }
  	    }],
		initComponent:function(){
			var me = this;
			Ext.define('ModelList', {
				extend : 'Ext.data.Model',
				fields : [ {
					name : 'id',
					type : 'int'
				}, 'code', 'type', 'name', 'unit', {
					name : 'content',
					type : 'double'
				}, {
					name : 'dtgcl',
					type : 'double'
				}, {
					name : 'singlePrice',
					type : 'double'
				}, {
					name : 'taxSinglePrice',
					type : 'double'
				},{
					name : 'singleSumPrice',
					type : 'double'
				},{
					name : 'taxSingleSumPrice',
					type : 'double'
				},{
					name : 'price',
					type : 'double'
				},{
					name : 'sumPrice',
					type : 'double'
				},'remark',{
					name : 'parentid',
					type : 'int'
				},{
					name : 'leaf',
					type : 'boolean'
				},{
					name : 'bitProjectId',
					type : 'int'
				},{
					name : 'lookTypeId',
					type : 'int'
				},{
					name : 'seq',
					type : 'int'
				}]
			});
			
			var bitProjectStore = Ext.create('Ext.data.TreeStore', {
				model : 'ModelList',
				autoLoad : true,
				pageSize : 10000,
			/*	root:{
					iconCls:'no-icon'
				},*/
				proxy : {
					type : 'ajax',
					url : appBaseUri + '/unitproject/getBitProjectItemInfo',
					extraParams : {"bitProjectId": me.bitProjectId},
					reader : {
						type : 'json',
						root : 'children'
					}
				}
			});
			
			var bitProjectColumns = [
            {
				text : "id",
				dataIndex : 'id',
				hidden : true
			},{
				text : "bitProjectId",
				dataIndex : 'bitProjectId',
				hidden : true
			},{
				text : "seq",
				dataIndex : 'seq',
				hidden : true
			},{
				text:'', width:25,xtype:'rownumberer'
			}
			,{
				text : "编码",
				dataIndex : 'code',
				sortable : false,
				width : '10%',
				xtype:'treecolumn',
				editor: {
					xtype:'triggerfield',
					triggerCls: Ext.baseCSSPrefix + 'form-search-trigger',
					editable:false,
					onTriggerClick:function(){
						Ext.create("Budget.app.bussinessProcess.ProjectItemWin", {
							bitItemGrid:Ext.getCmp("projectbitpanel-bititemgrid"),
							bitProjectId:me.bitProjectId
						}).show();
					}
				}
			}, {
				text : "类别",
				dataIndex : 'type',
				align:'center',
				sortable : false,
				width : '7%'
			}, {
				text : "名称",
				dataIndex : 'name',
				align:'center',
				sortable : false,
				width : '12%'
			}, {
				text : "单位",
				dataIndex : 'unit',
				width : '7%',
				sortable : false,
				align:'center'
			},{
				text : "含量",
				dataIndex : 'content',
				width : '5%',
				sortable : false,
				align:'right'
			},{
				text : "工程量",
				dataIndex : 'dtgcl',
				width : '7%',
				sortable : false,
				align:'right'
			},{
				text : "不含税单价",
				dataIndex : 'singlePrice',
				width : '8%',
				sortable : false,
				align:'right'
			},{
				text : "含税单价",
				dataIndex : 'taxSinglePrice',
				width : '6%',
				sortable : false,
				align:'right'
			},{
				text : "不含税合价",
				dataIndex : 'singleSumPrice',
				width : '8%',
				sortable : false,
				align:'right'
			},{
				text : "含税合价",
				dataIndex : 'taxSingleSumPrice',
				width : '6%',
				sortable : false,
				align:'right'
			},{
				text : "综合单价",
				dataIndex : 'price',
				width : '8%',
				sortable : false,
				align:'right'
			},{
				text : "综合合价",
				dataIndex : 'sumPrice',
				width : '8%',
				sortable : false,
				align:'right'
			},{
				text : "备注",
				dataIndex : 'remark',
				sortable : false,
				width : '12%'
			}];
			
			Ext.apply(this, {
				id:"projectbitpanel-bititemgrid",
				store : bitProjectStore,
				selModel : Ext.create('Ext.selection.CheckboxModel', {mode:'single', allowDeselect:true}),
				columns : bitProjectColumns,
				tbar : [ {
					xtype : 'button',
					iconCls : 'icon-add',
					text : '新增子目',
					scope : this,
					handler : me.newItemFun
				},{
					xtype : 'button',
					iconCls : 'icon-delete',
					text : '删除子目',
					scope : this,
					handler : me.deleteItemFun
				}],
				viewConfig:{
					loadingText : '正在查询数据，请耐心稍候...',
					stripeRows: true,
					enableTextSelection : true,
					getRowClass : function(record, rowIndex){
		            }
				},
				listeners : {
					'itemclick' : function(item, record) {
						Ext.getCmp('projectbitpanel-bitdetailgrid').getStore().load({
							params : {
								'unitProjectId' : record.get('id')
							}
						});
					}
				},  
	            defaults: {  
	                flex: 1  
	            }  
			});
			
			this.callParent(arguments);
		},
		newItemFun:function(){
			var grid = Ext.getCmp("projectbitpanel-bititemgrid");
			var records = grid.getSelectionModel().getSelection();
			var store = grid.getStore();
			
			var pos = store.getCount();
			var needOrderFlag = "N";
			//如果选选中了的话，会在之前插入一条，也就是需要刷当前的id包括之后的seq+1
			console.log(records[0]);
			if(records.length != 0){
				needOrderFlag = "Y";
				pos = records[0].data.index;
			}
			
			//新增一条空白的
			Ext.getCmp('projectbitpanel-bititemgrid').getEl().mask('数据处理中，请稍候...');
			Ext.Ajax.request({
				 url : appBaseUri + '/unitproject/insertBlankItem', //新增单位工程
				 params : {
					 needOrderFlag : needOrderFlag,
					 bitProjectId: Ext.getCmp('projectbitpanel-bititemgrid').bitProjectId,
					 pos: pos
				 },
				 method : "POST",
				 success : function(response) {
					 if (response.responseText != '') {
						 var res = Ext.JSON.decode(response.responseText);
						 if (res.success) {
							 store.reload();
						 } else {
							 globalObject.errTip(res.msg);
						 }
						 
						 Ext.getCmp('projectbitpanel-bititemgrid').getEl().unmask();
					 }
				 },
				 failure : function(response) {
					 globalObject.errTip('操作失败！');
				 }
			 });
		},
		deleteItemFun:function(){
			globalObject.confirmTip("删除该子目会同时删除下面板的运材安费用明细，确认删除吗?", 
					 function(btn){
						 if("yes" == btn){
							 var records = Ext.getCmp('projectbitpanel-bititemgrid').getSelectionModel().getSelection();	 
							 if(records.length == 0){
								 globalObject.infoTip("请选择至少一条子目进行删除.");
								 return;
							 }
							 
							Ext.getCmp('projectbitpanel-bititemgrid').getEl().mask('数据处理中，请稍候...');
							 Ext.Ajax.request({
								 url : appBaseUri + '/unitproject/deleteBitProjectItem',
								 params : {
									 "cmd":"edit",
									 "unitProjectId":records[0].data.id
								 },
								 method : "POST",
								 success : function(response) {
									 if (response.responseText != '') {
										 var res = Ext.JSON.decode(response.responseText);
										 if (res.success) {
											 Ext.getCmp('projectbitpanel-bititemgrid').getStore().reload();
											 Ext.getCmp('projectbitpanel-bitdetailgrid').getStore().removeAll();
										 } else {
											 globalObject.errTip(res.msg);
										 }
										 Ext.getCmp('projectbitpanel-bititemgrid').getEl().unmask();
									 }
								 },
								 failure : function(response) {
									 globalObject.errTip('操作失败！');
								 }
							 });
							
						 }
				 	});
		}
	});
	
	//详细
	Ext.define('Budget.app.bussinessProcess.ProjectBitPanel.BitDetailGrid', {
		extend : 'Ext.grid.Panel',
		region : 'south',
		height : '50%',
		header: false,
		initComponent:function(){
			var me = this;
			Ext.define('ModelList', {
				extend : 'Ext.data.Model',
				fields : [ {
					name : 'id',
					type : 'int'
				},  {
					name : 'unitprojectId',
					type : 'int'
				}, 'code', 'type', 'name', 'typeInfo', 'unit', {
					name : 'content',
					type : 'double'
				}, {
					name : 'amount',
					type : 'double'
				},  {
					name : 'singleSumPrice',
					type : 'double'
				},  {
					name : 'taxSingleSumPrice',
					type : 'double'
				},  {
					name : 'taxPrice',
					type : 'double'
				},  {
					name : 'noTaxPrice',
					type : 'double'
				}, {
					name : 'origCount',
					type : 'double'
				},{
					name : 'lookValueId',
					type : 'int'
				},{
					name : 'seq',
					type : 'int'
				}]
			});
			
			var bitProjectDetailStore = Ext.create('Ext.data.Store', {
				model : 'ModelList',
				autoLoad : false,
				pageSize : 10000,
				proxy : {
					type : 'ajax',
					url : appBaseUri + '/unitproject/getBitProjectDetailInfo',
					extraParams : {},
					reader : {
						type : 'json',
						root : 'data',
						totalProperty : 'totalRecord',
						successProperty : "success"
					}
				}
			});
			
			var bitProjectDetailColumns = [ {
				text : "id",
				dataIndex : 'id',
				hidden : true
			},{
				text:'', width:25,xtype:'rownumberer'
			},{
				text : "编码",
				dataIndex : 'code',
				hidden:true
			},{
				text : "编码",
				dataIndex : '',
				sortable : false,
				width : '10%',
				renderer:function(v, metv, record){
					return record.get('code').substr(record.get('code').indexOf('-') + 1);
				}
			},{
				text : "类别",
				dataIndex : 'type',
				sortable : false,
				align:'center',
				width : '5%'
			}, {
				text : "名称",
				dataIndex : 'name',
				sortable : false,
				width : '10%'
			}, {
				text : "规格及型号",
				dataIndex : 'typeInfo',
				sortable : false,
				width : '8%'
			}, {
				text : "单位",
				dataIndex : 'unit',
				sortable : false,
				align:'center',
				width : '5%'
			},{
				text : "含量",
				dataIndex : 'content',
				sortable : false,
				align:'right',
				width : '8%'
			},{
				text : "数量",
				dataIndex : 'amount',
				sortable : false,
				align:'right',
				width : '8%'
			},{
				text : "含税单价",
				dataIndex : 'taxPrice',
				sortable : false,
				align:'right',
				width : '8%'
			},{
				text : "不含税单价",
				dataIndex : 'noTaxPrice',
				sortable : false,
				align:'right',
				width : '8%'
			},{
				text : "含税合价",
				dataIndex : 'taxSingleSumPrice',
				sortable : false,
				align:'right',
				width : '8%'
			},{
				text : "不含税合价",
				dataIndex : 'singleSumPrice',
				sortable : false,
				align:'right',
				width : '8%'
			},{
				text : "原始含量",
				dataIndex : 'origCount',
				sortable : false,
				align:'right',
				width : '8%'
			},{
				text : "lookValueId",
				dataIndex : 'lookValueId',
				hidden:true
			},{
				text : "seq",
				dataIndex : 'seq',
				hidden:true
			}];
			
			Ext.apply(this, {
				store : bitProjectDetailStore,
				selModel : Ext.create('Ext.selection.CheckboxModel'),
				columns : bitProjectDetailColumns,
				tbar : [ {
					xtype : 'button',
					iconCls : 'icon-add',
					text : '新增运材安',
					scope : this,
					handler : me.newDetailFun
				},{
					xtype : 'button',
					iconCls : 'icon-delete',
					text : '删除运材安',
					scope : this,
					handler : me.deleteDetailFun
				}],
				viewConfig:{
					loadingText : '正在查询数据，请耐心稍候...',
					stripeRows: true,
					enableTextSelection : true,
					getRowClass : function(record, rowIndex){
		            }
				}
			});
			
			this.callParent(arguments);
		},
		newDetailFun:function(){
			
		},
		deleteDetailFun:function(){
			
		}
	});
	
});