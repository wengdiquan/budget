// 用户管理
Ext.onReady(function() {
	Ext.tip.QuickTipManager.init();
	// 费用代码维护
	Ext.define('Budget.app.bussinessProcess.YCATotal', {
		extend : 'Ext.panel.Panel',
		initComponent : function() {
			var me = this;
			Ext.apply(this, {
				layout : 'border',
				items : [  
				            	  Ext.create('Budget.app.bussinessProcess.YCATotal.CostTypeGrid'), 
								  Ext.create('Budget.app.bussinessProcess.YCATotal.CostValueGrid')
				 ]
			});
			this.callParent(arguments);
		}
	});
	
	//费用大类
	Ext.define('Budget.app.bussinessProcess.YCATotal.CostTypeGrid', {
		extend : 'Ext.tree.Panel',
		id : 'ycatotal-costtypegrid',
		region : 'west',
		width : '18%',
		border : true,
		title:"大类名称",
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
						
						Ext.getCmp('ycatotal-costvaluegrid').getStore().load({
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
	Ext.define('Budget.app.bussinessProcess.YCATotal.CostValueGrid', {
		extend : 'Ext.grid.Panel',
		id : 'ycatotal-costvaluegrid',
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
					extraParams : {"lookTypeId": Ext.getCmp('ycatotal-costtypegrid').lookTypeId},
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
			}, {
				text : "类别",
				dataIndex : 'type',
				sortable : false,
				width : '5%'
			},{
				text : "名称",
				dataIndex : 'name',
				sortable : false,
				width : '14%'
			},{
				text : "单位",
				dataIndex : 'unit',
				sortable : false,
				width : '5%',
				align:'center',
				renderer:rendenerYN
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
				selModel : Ext.create('Ext.selection.CheckboxModel', {mode:'single'/*, allowDeselect:true*/}),
				columns : costValueColumns,
				/*tbar : [ {
					xtype : 'button',
					iconCls : 'icon-add',
					text : '新增费用代码',
					scope : this,
					handler : me.newCodeCodeFun
				},{
					xtype : 'button',
					iconCls : 'icon-edit',
					text : '修改费用代码',
					scope : this,
					handler : me.editCodeCodeFun
				}],*/
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
		newCodeCodeFun: function(){
			var me = this;
			var looktypeId = Ext.getCmp('ycatotal-costtypegrid').lookTypeId;
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
			var looktypeId = Ext.getCmp('ycatotal-costtypegrid').lookTypeId;
			if (!looktypeId) {
				globalObject.infoTip('请先选择费用大类！');
				return;
			};
			
			var grid = Ext.getCmp("ycatotal-costvaluegrid");
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