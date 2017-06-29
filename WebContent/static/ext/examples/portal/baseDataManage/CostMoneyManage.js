// 用户管理
Ext.onReady(function() {
	Ext.tip.QuickTipManager.init();
	Ext.define('App.baseDataManage.CostMoneyManage.InfoWindow', {
		extend : 'Ext.window.Window',
		constructor : function(config) {
			config = config || {};
			Ext.apply(config, {
				title : '费用小类',
				width : 350,
				height : 280,
				bodyPadding : '10 5',
				modal : true,
				layout : 'fit',
				items : [ {
					xtype : 'form',
					fieldDefaults : {
						labelAlign : 'left',
						labelWidth : 90,
						anchor : '100%'
					},
					items : [{
						name : "cmd",
						xtype : "hidden",
						value : 'new'
					}, {
						xtype : 'hiddenfield',
						name : 'lookvalueId'
					},{
						xtype : 'hiddenfield',
						name : 'lookTypeId'
					},{
						xtype : 'textfield',
						name : 'lookvalueName',
						fieldLabel:'费用名称<font color="red">*</font>',
						allowBlank : false
					},{
						xtype : 'textfield',
						name : 'lookvalueCode',
						allowBlank : false,
						fieldLabel:'费用代码<font color="red">*</font>'
					},{
						xtype : 'textarea',
						name : 'remark',
						fieldLabel:'备注'
					},{xtype:'radiogroup', fieldLabel:'是否有效',   
		                columnWidth:0.7,items: [  
		                    { boxLabel: '是', name: 'enableFlag', inputValue: '1' , checked:true},   
		                    { boxLabel: '否', name: 'enableFlag', inputValue: '0' }
		                  
		            ]}],
					buttons : [ '->', {
						id : 'infowindow-save',
						text : '保存',
						iconCls : 'icon-save',
						width : 80,
						handler : function(btn, eventObj) {
							var window = btn.up('window');
							var form = window.down('form').getForm();
							if (form.isValid()) {
								window.getEl().mask('数据保存中，请稍候...');
								var vals = form.getValues();
								Ext.Ajax.request({
									url : appBaseUri + '/lookvalue/saveOrUpdateValue',
									params : vals,
									method : "POST",
									success : function(response) {
										if (response.responseText != '') {
											var res = Ext.JSON.decode(response.responseText);
											if (res.success) {
												globalObject.msgTip('操作成功！');
												Ext.getCmp('costmoneymanage-costvaluegrid').getStore().reload();
											} else {
												globalObject.errTip(res.msg);
											}
										}
									},
									failure : function(response) {
										globalObject.errTip('操作失败！');
									}
								});
								window.getEl().unmask();
								window.close();
							}
						}
					}, {
						id : 'infowindow-cancel',
						text : '取消',
						iconCls : 'icon-cancel',
						width : 80,
						handler : function() {
							this.up('window').close();
						}
					},'->']
				} ]
			});
			App.baseDataManage.CostMoneyManage.InfoWindow.superclass.constructor.call(this, config);
		}
	});
	
	// 费用代码维护
	Ext.define('Budget.app.baseDataManage.CostMoneyManage', {
		extend : 'Ext.panel.Panel',
		initComponent : function() {
			var me = this;
			Ext.apply(this, {
				layout : 'border',
				items : [ Ext.create('Budget.app.baseDataManage.CostMoneyManage.CostTypeGrid'), 
						  Ext.create('Budget.app.baseDataManage.CostMoneyManage.CostValueGrid') ]
			});
			this.callParent(arguments);
		}
	});
	
	//费用大类
	Ext.define('Budget.app.baseDataManage.CostMoneyManage.CostTypeGrid', {
		extend : 'Ext.tree.Panel',
		id : 'costmoneymanage-costtypegrid',
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
						me.lookTypeId = record.get('id');
						var lookTypeId = me.lookTypeId;
						if(record.get('id') == 0){
							lookTypeId = '';
						}
						
						Ext.getCmp('costmoneymanage-costvaluegrid').getStore().load({
							params : {
								'lookTypeId' : lookTypeId
							}
						});
					}
				}
			});
			this.callParent(arguments);
		}
	});

	// 详细值
	Ext.define('Budget.app.baseDataManage.CostMoneyManage.CostValueGrid', {
		extend : 'Ext.grid.Panel',
		id : 'costmoneymanage-costvaluegrid',
		plain : true,
		region : 'center',
		initComponent : function() {
			var me = this;
			Ext.define('CostValueList', {
				extend : 'Ext.data.Model',
				idProperty : 'lookvalue_id',
				fields : [ {
					name : 'lookvalueId',
					type : 'int'
				}, 'lookvalueName', 'lookvalueCode', 'remark', 'enableFlag' ]
			});
			
			var costValueStore = Ext.create('Ext.data.Store', {
				model : 'CostValueList',
				autoLoad : true,
				remoteSort : true,
				pageSize : globalPageSize,
				proxy : {
					type : 'ajax',
					url : appBaseUri + '/lookvalue/queryLookValueInfo',
					extraParams : {"lookTypeId": Ext.getCmp('costmoneymanage-costtypegrid').lookTypeId},
					reader : {
						type : 'json',
						root : 'data',
						totalProperty : 'totalRecord',
						successProperty : "success"
					}
				}
			});
			var costValueColumns = [ {
				text : "lookvalueId",
				dataIndex : 'lookvalueId',
				hidden : true
			},{
				text:'', width:25,xtype:'rownumberer'
			}
			,{
				text : "费用名称",
				dataIndex : 'lookvalueName',
				sortable : false,
				width : '30%'
			}, {
				text : "费用代码",
				dataIndex : 'lookvalueCode',
				sortable : false,
				width : '14%'
			},{
				text : "备注",
				dataIndex : 'remark',
				sortable : false,
				width : '14%'
			},{
				text : "是否有效",
				dataIndex : 'enableFlag',
				sortable : false,
				width : '10%',
				align:'center',
				renderer:rendenerYN
			}];
			Ext.apply(this, {
				store : costValueStore,
				selModel : Ext.create('Ext.selection.CheckboxModel'),
				columns : costValueColumns,
				tbar : [ {
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
			
			costValueStore.on('beforeload',function(){
				Ext.apply(costValueStore.proxy.extraParams, {"lookTypeId": Ext.getCmp('costmoneymanage-costtypegrid').lookTypeId});
			});
			
			this.callParent(arguments);
		},
		newCodeCodeFun: function(){
			var me = this;
			var looktypeId = Ext.getCmp('costmoneymanage-costtypegrid').lookTypeId;
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
			var looktypeId = Ext.getCmp('costmoneymanage-costtypegrid').lookTypeId;
			if (!looktypeId) {
				globalObject.infoTip('请先选择费用大类！');
				return;
			};
			
			var grid = Ext.getCmp("costmoneymanage-costvaluegrid");
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