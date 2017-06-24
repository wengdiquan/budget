//分部分项
Ext.onReady(function() {
	Ext.tip.QuickTipManager.init();
	Ext.define('Budget.app.bussinessProcess.ProjectBitPanel', {
		extend : 'Ext.panel.Panel',
		width: "100%",  
	    defaults: {  
           collapsible: true, // 支持该区域的展开和折叠  
           split: true// 支持用户拖放改变该区域的大小  
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
		extend : 'Ext.grid.Panel',
		region : 'north',
		height : '50%',
		header: false,
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
				idProperty : 'id',
				fields : [ {
					name : 'id',
					type : 'long'
				}, 'code', 'unit', 'type', 'name', {
					name : 'amount',
					type : 'double'
				}, {
					name : 'dtgcl',
					type : 'double'
				},  {
					name : 'price',
					type : 'double'
				}, 'remark',{
					name : 'parentid',
					type : 'long'
				},{
					name : 'leaf',
					type : 'long'
				},
				{
					name : 'bitProjectId',
					type : 'long'
				}]
			});
			
			var bitProjectStore = Ext.create('Ext.data.Store', {
				model : 'ModelList',
				autoLoad : true,
				remoteSort : true,
				pageSize : 10000,
				proxy : {
					type : 'ajax',
					url : appBaseUri + '/project/getBitProjectInfo',
					extraParams : {"bitProjectId": me.bitProjectId},
					reader : {
						type : 'json',
						root : 'data',
						totalProperty : 'totalRecord',
						successProperty : "success"
					}
				}
			});
			
			var bitProjectColumns = [ {
				text : "id",
				dataIndex : 'id',
				hidden : true
			},{
				text : "bitProjectId",
				dataIndex : 'bitProjectId',
				hidden : true
			},{
				text:'', width:25,xtype:'rownumberer'
			}
			,{
				text : "编码",
				dataIndex : 'code',
				sortable : false,
				width : '10%',
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
				width : '7%'
			}, {
				text : "名称",
				dataIndex : 'name',
				width : '10%'
			}, {
				text : "单位",
				dataIndex : 'unit',
				width : '7%'
			},{
				text : "含量",
				dataIndex : 'amount',
				width : '8%'
			},{
				text : "工程量",
				dataIndex : 'dtgcl',
				width : '8%'
			},{
				text : "合价",
				dataIndex : 'singlePrice',
				width : '8%'
			},{
				text : "综合单价",
				dataIndex : 'price',
				width : '8%'
			},{
				text : "综合合价",
				dataIndex : 'sumPrice',
				width : '8%'
			},{
				text : "备注",
				dataIndex : 'remark',
				sortable : false,
				width : '12%'
			}];
			
			Ext.apply(this, {
				id:"projectbitpanel-bititemgrid",
				store : bitProjectStore,
				selModel : Ext.create('Ext.selection.CheckboxModel'),
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
				}
			});
			
			this.callParent(arguments);
		},
		newItemFun:function(){
			var grid = Ext.getCmp("projectbitpanel-bititemgrid");
			var store = grid.getStore();
			store.add({});
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
							 
							 Ext.getCmp('projectbitpanel-bititemgrid').getStore().remove(records);
							 
							/* Ext.getCmp('projectinfo-projecttreeid').getEl().mask('数据处理中，请稍候...');
							 Ext.Ajax.request({
								 url : appBaseUri + '/project/saveOrUpdateProject',
								 params : {
									 "cmd":"edit",
									 "projectId":records[0].data.id
								 },
								 method : "POST",
								 success : function(response) {
									 if (response.responseText != '') {
										 var res = Ext.JSON.decode(response.responseText);
										 if (res.success) {
											 records[0].remove();
										 } else {
											 globalObject.errTip(res.msg);
										 }
									 }
								 },
								 failure : function(response) {
									 globalObject.errTip('操作失败！');
								 }
							 });
							 
							 Ext.getCmp('projectinfo-projecttreeid').getEl().unmask();*/
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
				idProperty : 'id',
				fields : [ {
					name : 'id',
					type : 'long'
				},  {
					name : 'unitprojectId',
					type : 'long'
				}, 'code', 'type', 'name', 'typeInfo', 'unit', {
					name : 'content',
					type : 'double'
				}, {
					name : 'singlePrice',
					type : 'double'
				},  {
					name : 'marketPrice',
					type : 'double'
				}, 'remark',{
					name : 'amount',
					type : 'double'
				},{
					name : 'origCount',
					type : 'double'
				}]
			});
			
			var bitProjectDetailStore = Ext.create('Ext.data.Store', {
				model : 'ModelList',
				autoLoad : false,
				pageSize : 10000,
				proxy : {
					type : 'ajax',
					url : appBaseUri + '/project/getBitProjectDetailInfo',
					extraParams : {"bitProjectId": me.bitProjectId},
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
			}
			,{
				text : "编码",
				dataIndex : 'code',
				sortable : false,
				width : '10%'
			}, {
				text : "类别",
				dataIndex : 'type',
				width : '7%'
			}, {
				text : "名称",
				dataIndex : 'name',
				width : '10%'
			}, {
				text : "规格及型号",
				dataIndex : 'type_info',
				width : '10%'
			}, {
				text : "单位",
				dataIndex : 'unit',
				width : '7%'
			},{
				text : "含量",
				dataIndex : 'content',
				width : '8%'
			},{
				text : "数量",
				dataIndex : 'amount',
				width : '8%'
			},{
				text : "市场价",
				dataIndex : 'marketPrice',
				width : '8%'
			},
			{
				text : "合价",
				dataIndex : 'singlePrice',
				width : '8%'
			},{
				text : "原始含量",
				dataIndex : 'origCount',
				width : '8%'
			}];
			
			Ext.apply(this, {
				store : bitProjectDetailStore,
				selModel : Ext.create('Ext.selection.CheckboxModel'),
				columns : bitProjectDetailColumns,
				tbar : [ {
					xtype : 'button',
					iconCls : 'icon-add',
					text : '新增模块',
					scope : this,
					handler : me.newDetailFun
				},{
					xtype : 'button',
					iconCls : 'icon-delete',
					text : '删除模块',
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