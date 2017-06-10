Ext.define('Cscec.app.TimeLineManage.TimeNodeGrid', {
		extend : 'Ext.ux.custom.GlobalGridPanel',
		region : 'center',
		idProperty:"timeNodeId",
		_timeLineId:"",
		hideBBar: true,
		plugins:[{
  	        ptype: 'cellediting',
  	        clicksToEdit: 1,
  	        editing:false, 
  	        listeners:{
  	        	edit:function(editor , context , eOpts){
  	        	}
  	        }
  	    }],
  	    buttonAlign:"center",
  	    buttons:[{
			xtype : 'button',
			itemId : 'btnSave',
			iconCls : 'icon-save',
			text : '全部保存',
			scope : this,
			// disabled : !globalObject.haveAction(me.cName + 'Export'),
			handler : function() {
				me.onExportClick();
			}
		},{
			xtype : 'button',
			itemId : 'btnCancel',
			iconCls : 'icon-cancel',
			text : '取消',
			scope : this,
			handler : function() {
				Ext.getCmp("TimeLineManage.TimeNodeGrid").close();
			}
		}],
		initComponent : function() {
			var me = this;
			Ext.define('ModelList', {
				extend : 'Ext.data.Model',
				idProperty : 'lookValueId',
				fields : [ {
					name : 'lookValueId',
					type : 'long'
				},'timeLineName','lookValueCode','lookValueName']
			});

			//显示列表
			var store = Ext.create('Ext.data.Store', {
				model : 'ModelList',
				remoteSort : true,
				pageSize : 1000,
				proxy : {
					type : 'ajax',
					url : appBaseUri + '/sys/timenode/timenodelooklist',
					extraParams : me.extraParams,
					actionMethods : {
						read : 'POST'
					},
					reader : {
						type : 'json',
						root : 'data',
						totalProperty : 'totalRecord',
						successProperty : "success"
					}
				}
			});

			var columns = [  {text:'序号', width:40,xtype:'rownumberer', align:"center"},{
				text : "ID",
				dataIndex : 'lookValueId',
				width : '3%',
				hidden:true
			},  {
				text : "节点代码",
				dataIndex : 'lookValueCode',
				hidden:true
			}, {
				text : "节点名称",
				dataIndex : 'lookValueName',
				width : '20%',
				sortable : false
			},  {
				text : "节点结束时间",
				dataIndex : 'nodeEndTime',
				width : '14%',
				align:"center",
				sortable : false,
				editor:new Ext.form.DateField({  
                    format:"Y-m-d", 
                	labelWidth:70,
                    width:300
                }),
             	renderer:Ext.util.Format.dateRenderer('Y-m-d')
			},{
				text : "合同设置",
				dataIndex : '',
				width : '14%',
				sortable : false,
				align:"center",
				renderer:function(value, metaV, record){
					return "<a href='javascript:;' onclick='window.openContrctWin(" + record.get('timeLineId')+ ")' style='text-decoration:none'>合同设置</a>"
				}
			},{
				text : "保存",
				dataIndex : '',
				width : '14%',
				sortable : false,
				align:"center",
				renderer:function(value, metaV, record){
					return "<a href='javascript:;' onclick='window.openContrctWin(" + record.get('timeLineId')+ ")' style='text-decoration:none'>保存</a>"
				}
			}];
			
			//这是实现父类的方法
			Ext.apply(this, {
				id : 'timenodegrid',
				store : store,
				columns : columns
			});
			store.loadPage(1);
			this.callParent(arguments);
		},
		onAddClick : function() {
		},
		onEditClick : function(){
		},
		onViewClick : function() {
		}
	});