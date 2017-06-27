// 用户管理
Ext.onReady(function() {
	Ext.tip.QuickTipManager.init();
	// 费用汇总
	Ext.define('Budget.app.bussinessProcess.FeeTotal', {
		extend : 'Ext.panel.Panel',
		initComponent : function() {
			var me = this;
			Ext.apply(this, {
				layout: 'border',
				items : [ 
						            	  Ext.create('Budget.app.bussinessProcess.FeeTotal.FeeDetailGrid'),
						            	  {	
								              region:'center', 
								              id: 'center-region-container11', 
								              layout: 'border', 
								              items : [
								            	  			Ext.create('Budget.app.bussinessProcess.FeeTotal.FeeTypeGrid'),
								            	  			Ext.create('Budget.app.bussinessProcess.FeeTotal.FeeValueGrid')
								                       ]
								         }
				    ]
			});
			this.callParent(arguments);
		}
	});
	
	// 详细值
	Ext.define('Budget.app.bussinessProcess.FeeTotal.FeeDetailGrid', {
		extend : 'Ext.grid.Panel',
		id : 'feetotal-feedetailgrid',
		plain : true,
		region : 'north',
		height : '60%',
		border:true,
		initComponent : function() {
			var me = this;
			Ext.define('FeeValueList', {
				extend : 'Ext.data.Model',
				idProperty : 'id',
				fields : [ {
					name : 'id',
					type : 'int'
				}, 'seq', 'feeCode', 'name', 'calculatedRadix','radixRemark','rate','amount', 'remark', 'templetId']
			});
			
			var costValueStore = Ext.create('Ext.data.Store', {
				model : 'FeeValueList',
				autoLoad : true,
				remoteSort : true,
				pageSize : globalPageSize,
				proxy : {
					type : 'ajax',
					url : appBaseUri + '/feetotal/queryFeeTotalInfo',
					extraParams : {"templetId": 1},
					reader : {
						type : 'json',
						root : 'data',
						totalProperty : 'totalRecord',
						successProperty : "success"
					}
				}
			});
			var costValueColumns = [ {
				text : "序号",
				dataIndex : 'seq',
				width : '7%',
				hidden : false
			},{
				text : "费用代号",
				dataIndex : 'feeCode',
				sortable : false,
				width : '7%'
			}, {
				text : "名称",
				dataIndex : 'name',
				sortable : false,
				width : '20%'
			},{
				text : "计算基数",
				dataIndex : 'calculatedRadix',
				sortable : false,
				width : '20%'
			},{
				text : "基数说明",
				dataIndex : 'radixRemark',
				sortable : false,
				width : '20%'
			},{
				text : "费率(%)",
				dataIndex : 'rate',
				sortable : false,
				width : '10%'
			},{
				text : "金额",
				dataIndex : 'amount',
				sortable : false,
				width : '10%'
			},{
				text : "备注",
				dataIndex : 'remark',
				sortable : false,
				width : '10%'
			},{
				text : "id",
				dataIndex : 'id',
				hidden : true
			},{
				text : "templetId",
				dataIndex : 'templetId',
				hidden : true
			}];
			Ext.apply(this, {
				store : costValueStore,
				listeners : {
					'itemclick' : function(item, record) {
						if(record.data.id == 0){
							return;
						}
						me.feeTotalId = record.data.id;
					}
				},
				selModel : Ext.create('Ext.selection.CheckboxModel', {mode:'single'/*, allowDeselect:true*/}),
				columns : costValueColumns,
				tbar : [ {
					xtype : 'button',
					iconCls : 'icon-add',
					text : '保存模板',
					scope : this,
					handler : me.newCodeCodeFun
				}],
				bbar : Ext.create('Ext.PagingToolbar', {
					store : costValueStore,
					displayInfo : true
				}),
				viewConfig:{
					loadingText : '正在查询数据，请耐心稍候...',
					stripeRows:false,
					enableTextSelection : true,
					getRowClass : function(record, rowIndex){
						if(record.get('enableFlag') == '0'){
		                    return 'info_rp';
						}
		            }
				}
			});
			
		/*	costValueStore.on('beforeload',function(){
				Ext.apply(costValueStore.proxy.extraParams, {"parentId": Ext.getCmp('categoryandmodelmanage-cmgrid').parentId});
			});*/
			
			this.callParent(arguments);
		},
		newCodeCodeFun: function(){
			var me = this;
			var dataall = Ext.getCmp('feetotal-feedetailgrid').getStore();
			if (!parentId) {
				globalObject.infoTip('请先选模块名称！');
				return;
			};
			var win = new App.bussinessProcess.FeeTotal.InfoWindowCM({
				hidden : true
			});
			var form = win.down('form').getForm();
			form.findField('parentId').setValue(parentId);
			win.show();
		}
	});
	
	Ext.define('Budget.app.bussinessProcess.FeeTotal.FeeTypeGrid', {
		extend : 'Ext.tree.Panel',
		id : 'feetotal-feetypegrid',
		region : 'west',
		width : '18%',
		border : true,
		title:"费用名称",
		animate : true,//动画效果
		initComponent : function() {
			var me = this;
			var costTypeStore = Ext.create('Ext.data.TreeStore', {
				autoLoad : true,
				proxy : {
					type : 'ajax',
					url : appBaseUri + '/lookvalue/queryLookTypeInfo',
					extraParams : {listCode:"COST_CODE"},
					reader : {
						type : 'json',
						root : 'children'
					}
				}
			});
			Ext.apply(this, {
				rootVisible : false,
				store : costTypeStore,
				listeners : {
					'itemclick' : function(item, record) {
						if(record.get('id') == 0){
							return;
						}
						
						me.lookTypeId = record.get('id');
						
						Ext.getCmp('feetotal-feevaluegrid').getStore().load({
							params : {
								'lookTypeId' : me.lookTypeId
							}
						});
					}
				}
			});
			this.callParent(arguments);
		}
	});

	// 详细值
	Ext.define('Budget.app.bussinessProcess.FeeTotal.FeeValueGrid', {
		extend : 'Ext.grid.Panel',
		id : 'feetotal-feevaluegrid',
		plain : true,
		region : 'center',
		initComponent : function() {
			var me = this;
			Ext.define('TotalYCAList', {
				extend : 'Ext.data.Model',
				idProperty : 'id',
				fields : [ {
					name : 'id',
					type : 'int'
				}, 'code', 'type', 'name', 'unit', 'tax_Price', 'notax_Price', 'amount', 'tax_Single_SumPrice', 'single_SumPrice'  ]
			});
			
			var costValueStore = Ext.create('Ext.data.Store', {
				model : 'TotalYCAList',
				autoLoad : true,
				remoteSort : true,
				pageSize : globalPageSize,
				proxy : {
					type : 'ajax',
					url : appBaseUri + '/ycatotal/queryYCATotalInfo',
					extraParams : {"lookTypeId": Ext.getCmp('feetotal-feetypegrid').lookTypeId},
					reader : {
						type : 'json',
						root : 'data',
						totalProperty : 'totalRecord',
						successProperty : "success"
					}
				}
			});
			var costValueColumns = [ {
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
				width : '14%'
			},{
				text : "名称",
				dataIndex : 'name',
				sortable : false,
				width : '14%'
			},{
				text : "含税单价",
				dataIndex : 'tax_Price',
				sortable : false,
				width : '10%'
			},{
				text : "不含税单价",
				dataIndex : 'notax_Price',
				sortable : false,
				width : '10%'
			},{
				text : "数量",
				dataIndex : 'amount',
				sortable : false,
				width : '10%'
			},{
				text : "含税合价",
				dataIndex : 'tax_Single_SumPrice',
				sortable : false,
				width : '10%'
			},{
				text : "不含税合价",
				dataIndex : 'single_SumPrice',
				sortable : false,
				width : '10%'
			}];
			Ext.apply(this, {
				store : costValueStore,
				selModel : Ext.create('Ext.selection.Model'),
				columns : costValueColumns,
				bbar : Ext.create('Ext.PagingToolbar', {
					store : costValueStore,
					displayInfo : true
				}),
				viewConfig:{
					loadingText : '正在查询数据，请耐心稍候...',
					stripeRows:false,
					enableTextSelection : true,
					getRowClass : function(record, rowIndex){
						if(record.get('enableFlag') == '0'){
		                    return 'info_rp';
						}
		            }
				}
			});
			
			this.callParent(arguments);
		},
		listeners : {
			'itemdblclick' : function(_this, record, td, cellIndex, tr, rowIndex, e, eOpts){
				var feeTotalId = Ext.getCmp('feetotal-feedetailgrid').feeTotalId;
				if (!feeTotalId) {
					globalObject.infoTip('请先选择一条费用汇总记录！');
					return;
				} else {  
		        	var data = Ext.getCmp('feetotal-feedetailgrid').getSelectionModel().getSelection();  
                    var vals = {
                    		id : feeTotalId,
                    		calculatedRadix : data[0].get("calculatedRadix") + "+" + record.data.code ,
                    		radixRemark : data[0].get("radixRemark") + "+" + record.data.name,
                    		cmd : "edit"
                    };
                    Ext.Ajax.request({
						url : appBaseUri + '/feetotal/saveOrUpdateValue',
						params : vals,
						method : "POST",
						success : function(response) {
							if (response.responseText != '') {
								var res = Ext.JSON.decode(response.responseText);
								if (res.success) {
									globalObject.msgTip('操作成功！');
									//Ext.getCmp('feetotal-feedetailgrid').getStore().reload();
									Ext.getCmp('feetotal-feedetailgrid').getStore().loadPage(1);
								} else {
									globalObject.errTip(res.msg);
								}
							}
						},
						failure : function(response) {
							globalObject.errTip('操作失败！');
						}
					});
		        }  
			}
		},
		newCodeCodeFun: function(){
			var me = this;
			var looktypeId = Ext.getCmp('feetotal-feetypegrid').lookTypeId;
			if (!looktypeId) {
				globalObject.infoTip('请先选择费用大类！');
				return;
			};
			var win = new App.baseDataManage.CostMoneyManage.InfoWindow({
				hidden : true
			})
			var form = win.down('form').getForm();
			form.findField('lookTypeId').setValue(looktypeId);
			win.show();
		},
		editCodeCodeFun : function() {
			var me = this;
			var looktypeId = Ext.getCmp('feetotal-feetypegrid').lookTypeId;
			if (!looktypeId) {
				globalObject.infoTip('请先选择费用大类！');
				return;
			};
			
			var grid = Ext.getCmp("feetotal-feevaluegrid");
			var records = grid.getSelectionModel().getSelection()
			if(records.length != 1){
				globalObject.infoTip('请先需要修改的费用信息！');
				return;
			}
			
			var gridRecord = grid.getStore().findRecord('lookvalueId', records[0].get('lookvalueId'));
			
			var win = new App.baseDataManage.CostMoneyManage.InfoWindow({
				hidden : true
			});
			
			var form = win.down('form').getForm();
			form.findField('lookTypeId').setValue(looktypeId);
			form.findField('cmd').setValue('edit');
			form.findField('lookvalueName').setReadOnly(true);
			form.findField('lookvalueName').setDisabled(true);
			form.findField('lookvalueCode').setReadOnly(true);
			form.findField('lookvalueCode').setDisabled(true);
			form.loadRecord(gridRecord);
			win.show();
		}
	});
	
});