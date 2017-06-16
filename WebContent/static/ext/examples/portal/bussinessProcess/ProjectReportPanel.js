
//项目报表
Ext.onReady(function() {
	Ext.tip.QuickTipManager.init();
	
	Ext.define('Budget.app.bussinessProcess.ProjectReportPanel', {
		extend : 'Ext.panel.Panel',
		initComponent : function() {
			var me = this;
			Ext.apply(this, {
				layout : 'border',
				items : []
			});
			this.callParent(arguments);
		}
	});
	
	
});