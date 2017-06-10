// 合同设置
Ext.onReady(function() {
	Ext.tip.QuickTipManager.init();

	Ext.define('Cscec.app.ContractManage.ContractSetWindow', {
		extend : 'Ext.window.Window',
		constructor : function(config) {
			config = config || {};
			Ext.apply(config, {
				title : '合同设置',
				width : 350,
				height : 440,
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
					items : [ {                  //定义隐藏参数
						name : "cmd",
						xtype : "hidden",
						value : 'new'
					}, {
						xtype : 'hiddenfield',
						name : 'contractId'
					}, {
						xtype : 'textfield',
						name : 'contractName',
						fieldLabel : '合同名称',
						allowBlank : false
					},{
						xtype : 'textfield',
						name : 'contractType',
						fieldLabel : '合同编码',
						allowBlank : false
					}],
					buttons : [ '->', {          //定义保存
						id : 'ContractSet-save',
						text : '保存',
						iconCls : 'icon-save',
						width : 80,
						handler : function(btn, eventObj) {
							var window = btn.up('window');
							var form = window.down('form').getForm();
							
							if (form.isValid()) {
								var vals = form.getValues();
//								if (isNaN(vals['sortOrder'])) {
//									globalObject.errTip('排序项只能输入数字！');
//									return;
//								}
								window.getEl().mask('数据保存中，请稍候...');
								var sb = new StringBuffer();
								Ext.Ajax.request({
									url : appBaseUri + '/sys/contract/saveContract',
									params : {   //这里设置保存所有提交合同字段
										cmd : vals['cmd'],
										contractId : vals['contractId'],
										contractName : vals['contractName'],
										contractType : vals['contractType']										
									},
									method : "POST",
									success : function(response) {
										if (response.responseText != '') {
											var res = Ext.JSON.decode(response.responseText);
											if (res.success) {
												globalObject.msgTip('操作成功！');
												Ext.getCmp('contractsetgrid').getStore().reload();
											} else {
												//globalObject.msgTip('菜单代号不能重复！');
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
						id : 'ContractSet-cancel',
						text : '取消',
						iconCls : 'icon-cancel',
						width : 80,
						handler : function() {
							this.up('window').close();
						}
					}, {
						id : 'ContractSet-accept',
						text : '确定',
						hidden : true,
						iconCls : 'icon-accept',
						width : 80,
						handler : function() {
							this.up('window').close();
						}
					}, '->' ]
				} ]
			});
			Cscec.app.ContractManage.ContractSetWindow.superclass.constructor.call(this, config);
		}
	});

	//这是啥?????????????????????????????????????????????????????????????????????????????????????????????????????????
	Ext.define('Cscec.app.ContractManage.ContractSet', {
		extend : 'Ext.ux.custom.GlobalGridPanel',
		region : 'center',
		idProperty:"contractId",
		initComponent : function() {
			var me = this;

			Ext.define('ModelList', {
				extend : 'Ext.data.Model',
				idProperty : 'contractId',
				fields : [ {
					name : 'contractId',
					type : 'long'
				},'contractName','contractType']
			});


			//显示列表
			var store = me.createStore({
				modelName : 'ModelList',
				proxyUrl : appBaseUri + '/sys/contract/conlist',
				proxyDeleteUrl : appBaseUri + '/sys/contract/deleteContract',
				extraParams : me.extraParams,
				sortProperty : 'contractId',
				sortDirection : 'ASC'
			});

			var columns = [ {
				text : "ID",
				dataIndex : 'contractId',
				width : '3%',
				sortable : false
			}, {
				text : "合同名称",
				dataIndex : 'contractName',
				width : '14%',
				sortable : false
			},  {
				text : "合同类型",
				dataIndex : 'contractType',
				width : '14%',
				sortable : false
			},{
				text : "合同代码",
				dataIndex : 'contractCode',
				width : '14%',
				sortable : false
			}];

			
			
			
			//这是实现父类的方法
			Ext.apply(this, {
				id : 'contractsetgrid',
				store : store,
				columns : columns
			});

			store.loadPage(1);

			this.callParent(arguments);
		},
		onAddClick : function() {
			new Cscec.app.ContractManage.ContractSetWindow().show();
		},
		onEditClick : function(){
			var grid = Ext.getCmp("contractsetgrid");
			var id = grid.getSelectionModel().getSelection()[0].get('contractId');
			var gridRecord = grid.getStore().findRecord('contractId', id);
			var win = new Cscec.app.ContractManage.ContractSetWindow({
				hidden : true,
				height : 230
			});
			var form = win.down('form').getForm();
			form.loadRecord(gridRecord);
			form.findField("cmd").setValue("edit");
			Ext.getCmp('ContractSet-accept').hide();
			Ext.getCmp('ContractSet-save').show();
			Ext.getCmp('ContractSet-cancel').show();
			win.show();
		},
		onViewClick : function() {
			var grid = Ext.getCmp("contractsetgrid");
			var id = grid.getSelectionModel().getSelection()[0].get('contractId');
			var gridRecord = grid.getStore().findRecord('contractId', id);
			var win = new Cscec.app.ContractManage.ContractSetWindow({
				hidden : true,
				height : 230
			});
			var form = win.down('form').getForm();
			form.loadRecord(gridRecord);
			Ext.getCmp('ContractSet-save').hide();
			Ext.getCmp('ContractSet-cancel').hide();
			Ext.getCmp('ContractSet-accept').show();
			win.show();
		}
	});
});