Ext.onReady(function() {
	Ext.tip.QuickTipManager.init();
	Ext.define('App.baseDataManage.YCAManage.InfoWindow', {
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
						name : 'lookvalue_id'
					},{
						xtype : 'hiddenfield',
						name : 'id'
					},{
						xtype : 'textfield',
						name : 'code',
						allowBlank : false,
						fieldLabel:'费用代码<font color="red">*</font>'
					},{
						xtype : 'textfield',
						name : 'name',
						fieldLabel:'费用名称<font color="red">*</font>',
						allowBlank : false
					},{
						xtype : 'textfield',
						name : 'unit',
						fieldLabel:'单位<font color="red">*</font>'
					}],
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
									url : appBaseUri + '/yca/saveOrUpdateValue',
									params : vals,
									method : "POST",
									success : function(response) {
										if (response.responseText != '') {
											var res = Ext.JSON.decode(response.responseText);
											if (res.success) {
												globalObject.msgTip('操作成功！');
												Ext.getCmp('ycamanage-costvaluegrid').getStore().reload();
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
			App.baseDataManage.YCAManage.InfoWindow.superclass.constructor.call(this, config);
		}
	});

	// 费用代码维护
	Ext.define('Budget.app.baseDataManage.YCAManage', {
		extend : 'Ext.panel.Panel',
		initComponent : function() {
			var me = this;
			Ext.apply(this, {
				layout: 'border',
				items : [ 
				          Ext.create('Budget.app.baseDataManage.YCAManage.CostTypeGrid'), 
						  Ext.create('Budget.app.baseDataManage.YCAManage.CostValueGrid')
						  ]
			});
			this.callParent(arguments);
		}
	});
	
	//费用大类
	Ext.define('Budget.app.baseDataManage.YCAManage.CostTypeGrid', {
		extend : 'Ext.tree.Panel',
		id : 'ycamanage-costtypegrid',
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
					url : appBaseUri + '/yca/queryTreeList',
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
						if(record.data.id == 0){
							return;
						}
						
						me.lookValueId = record.data.id;
						
						Ext.getCmp('ycamanage-costvaluegrid').getStore().load({
							params : {
								'lookValueId' : me.lookValueId
							}
						});
					}
				}
			});
			this.callParent(arguments);
		}
	});

	// 详细值
	Ext.define('Budget.app.baseDataManage.YCAManage.CostValueGrid', {
		extend : 'Ext.grid.Panel',
		id : 'ycamanage-costvaluegrid',
		plain : true,
		region : 'center',
		initComponent : function() {
			var me = this;
			Ext.define('CostValueList', {
				extend : 'Ext.data.Model',
				idProperty : 'id',
				fields : [ {
					name : 'looktype_id',
					type : 'int'
				}, 'code', 'name', 'unit', 'category']
			});
			
			var costValueStore = Ext.create('Ext.data.Store', {
				model : 'CostValueList',
				autoLoad : false,
				remoteSort : true,
				pageSize : globalPageSize,
				proxy : {
					type : 'ajax',
					url : appBaseUri + '/yca/queryYCAInfo',
					extraParams : {"lookValueId": Ext.getCmp('ycamanage-costtypegrid').lookValueId},
					reader : {
						type : 'json',
						root : 'data',
						totalProperty : 'totalRecord',
						successProperty : "success"
					}
				}
			});
			var costValueColumns = [ {
				text : "费用代码",
				dataIndex : 'code',
				width : '14%',
				hidden : false
			},{
				text : "费用名称",
				dataIndex : 'name',
				sortable : false,
				width : '30%'
			}, {
				text : "单位",
				dataIndex : 'unit',
				sortable : false,
				width : '14%'
			},{
				text : "种类",
				dataIndex : 'category',
				sortable : false,
				width : '14%'
			},{
				text : "looktype_id",
				dataIndex : 'looktype_id',
				hidden : true
			}];
			Ext.apply(this, {
				store : costValueStore,
				selModel : Ext.create('Ext.selection.CheckboxModel'),
				columns : costValueColumns,
				tbar : [ {
					xtype : 'button',
					iconCls : 'icon-add',
					text : '新增',
					scope : this,
					handler : me.newCodeCodeFun
				},{
					xtype : 'button',
					iconCls : 'icon-edit',
					text : '修改',
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
			
			this.callParent(arguments);
		},
		newCodeCodeFun: function(){
			var me = this;
			var lookValueId = Ext.getCmp('ycamanage-costtypegrid').lookValueId;
			if (!lookValueId) {
				globalObject.infoTip('请先选择费用小类！');
				return;
			};
			var win = new App.baseDataManage.YCAManage.InfoWindow({
				hidden : true
			});
			var form = win.down('form').getForm();
			form.findField('lookvalue_id').setValue(lookValueId);
			win.show();
		},
		editCodeCodeFun : function() {
			var me = this;
			var lookValueId = Ext.getCmp('ycamanage-costtypegrid').lookValueId;
			if (!lookValueId) {
				globalObject.infoTip('请先选择费用小类！');
				return;
			};
			
			var grid = Ext.getCmp("ycamanage-costvaluegrid");
			var records = grid.getSelectionModel().getSelection();
			if(records.length != 1){
				globalObject.infoTip('请先选择需要修改的记录！');
				return;
			}
			
			var gridRecord = grid.getStore().findRecord('id', records[0].get('id'));
			
			var win = new App.baseDataManage.YCAManage.InfoWindow({
				hidden : true
			});
			
			var form = win.down('form').getForm();
			form.findField('cmd').setValue('edit');
			form.findField('code').setReadOnly(true);
			form.loadRecord(gridRecord);
			win.show();
		}
	});
});