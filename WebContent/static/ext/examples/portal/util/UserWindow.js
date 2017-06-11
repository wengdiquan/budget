/**
 * 用户查询框
 */
Ext.onReady(function() {
	Ext.tip.QuickTipManager.init();
	
	Ext.define('App.UserQuery', {
		extend : 'Ext.window.Window',
		constructor : function(config) {
			config = config || {};
			Ext.apply(config, {
				title : '用户查询',
				width : 350,
				height : 170,
				bodyPadding : '10 5',
				modal : true,
				layout : 'fit',
				items : [ {
					xtype : 'form',
					fieldDefaults : {
						labelAlign : 'right',
						labelWidth : 90,
						anchor : '95%'
					},
					items : [{
						xtype : 'textfield',
						name : 'name',
						fieldLabel : '用户名',
						emptyText : '请输入用户名'
					},
					{
						xtype : 'textfield',
						name : 'personCode',
						fieldLabel : '人员编码',
						emptyText : '请输入编码'
					}],
					buttons : [ '->',  {
						id : 'userquery-accept',
						text : '确定',
						iconCls : 'icon-accept',
						width : 80,
						handler : function(btn, eventObj) {
							var window = btn.up('window');
							var form = window.down('form').getForm();
							var vals = form.getValues();
							var store = Ext.getCmp('usermanagementgrid').getStore();
							
							var extraParams = {
								name:vals['name'],
								personCode:vals['personCode']
							};
							Ext.apply(store.proxy.extraParams, extraParams);
							store.reload();
							window.close();
						}
					}, {
						id : 'userquery-reset',
						text : '重置',
						iconCls : 'icon-reset',
						width : 80,
						handler : function() {
							this.up('window').down('form').getForm().reset();
						}
					},{
						id : 'userquery-cancel',
						text : '取消',
						iconCls : 'icon-cancel',
						width : 80,
						handler : function() {
							this.up('window').close();
						}
					}, '->' ]
				} ]
			});
			App.UserQuery.superclass.constructor.call(this, config);
		}
	});

	Ext.define('Budget.app.util.UserWindow', {
		extend : 'Ext.ux.custom.GlobalGridPanel',
		region : 'center',
		initComponent : function() {
			var me = this;
			Ext.define('ModelList', {
				extend : 'Ext.data.Model',
				idProperty : 'id',
				fields : [ {
					name : 'userId',
					type : 'long'
				}, 'personCode', 'name', 'personType', 'cardNumber', 'gender', 'age',
				{
					name : 'lastLoginTime',
					type : 'datetime',
					dateFormat : 'Y-m-d H:i:s'
				}]
			});

			var store = me.createStore({
				modelName : 'ModelList',
				proxyUrl : appBaseUri + '/sys/sysuser/getSysUser',
				proxyDeleteUrl : appBaseUri + '/sys/sysuser/deleteSysUser',
				extraParams : me.extraParams
			});

			var columns = [ {
				text : "ID",
				dataIndex : 'userId',
				width : '5%'
			}, {
				text : "用户名",
				dataIndex : 'name',
				width : '13%'
			}, {
				text : "人员编号",
				dataIndex : 'personCode',
				width : '10%'
			}, {
				text : "人员类别",
				dataIndex : 'personType',
				width : '14%'
			}, {
				text : "身份证号",
				dataIndex : 'cardNumber',
				width : '15%'
			},{
				text : "性别",
				dataIndex : 'gender',
				width : '15%'
			}, {
				text : "年龄",
				dataIndex : 'age',
				width : '15%'
			}, {
				text : "最后登录时间",
				dataIndex : 'lastLoginTime',
				width : '19%'
			} ];
			
			Ext.apply(this, {
				store : store,
				columns : columns
			});
			
			if(me.needAutoLoad){
				store.loadPage(1);
			}
			
			this.callParent(arguments);
		},
		onQueryClick:function() {
			var winQuery = new App.UserQuery();
			var form = winQuery.down('form').getForm();
			form.setValues(Ext.getCmp('userwindowgrid').getStore().proxy.extraParams)
			winQuery.show(); 
		}
	});
});